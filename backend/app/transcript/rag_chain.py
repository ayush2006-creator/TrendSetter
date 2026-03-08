import os
import json
from typing import List, Optional, TypedDict
from langchain_groq import ChatGroq
from langchain_core.documents import Document
from langchain_core.messages import HumanMessage, AIMessage
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langgraph.graph import StateGraph, END
from pydantic import BaseModel, Field
from langchain_aws import ChatBedrock
from app.transcript.query_pipeline import query_reels
from dotenv import load_dotenv
load_dotenv()

# ── Pydantic output schema (unchanged) ───────────────────────────────────────
class IdeaStructure(BaseModel):
    concept: str; hook: str; structure: List[str]
    emotion: str; why_it_works: str
    reference_url: str = ""
class OptimizationVariant(BaseModel):
    change: str; add: str; result: str

class OptimizationSuggestion(BaseModel):
    second_idea_emotional_variant: OptimizationVariant

class BestFitRecommendation(BaseModel):
    best_idea_index: int; reason: str

class AnalysisBlock(BaseModel):
    performance_drivers: List[str]; engagement_triggers: List[str]

class StrategistOutput(BaseModel):
    analysis: AnalysisBlock
    patterns: List[str]
    ideas: List[IdeaStructure]
    best_fit_recommendation: BestFitRecommendation
    optimization_suggestion: OptimizationSuggestion


# ── LangGraph state ───────────────────────────────────────────────────────────
class AgentState(TypedDict):
    query: str
    chat_history: List
    documents: List[Document]
    answer: Optional[StrategistOutput]
    sources: List[dict]

# _base_llm = ChatBedrock(
#     model_id="meta.llama3-70b-instruct-v1:0",
#     region_name="ap-south-1",
#     model_kwargs={
#         "max_gen_len": 4096,
#         "temperature": 0.7,
#     }
# )
# llm = _base_llm
# _base_llm = ChatBedrock(
#     model_id="anthropic.claude-3-haiku-20240307-v1:0",
#     region_name="ap-south-1",
#     model_kwargs={
#         "temperature": 0.7,
#         "max_tokens": 2048
#     }
# )
# llm = _base_llm.with_structured_output(StrategistOutput)
_base_llm = ChatBedrock(
    model_id="meta.llama3-70b-instruct-v1:0",
    region_name="ap-south-1",
    model_kwargs={
        "temperature": 0.7,
        "max_gen_len": 4096,
    }
)
llm = _base_llm
# ── LLM ──────────────────────────────────────────────────────────────────────
#_base_llm = ChatGroq(model="llama-3.3-70b-versatile", temperature=0.85, max_tokens=2048)
#llm = _base_llm.with_structured_output(StrategistOutput, method="json_mode")


# ── System prompt (unchanged) ─────────────────────────────────────────────────
# system_prompt = """
# You are an expert social media performance analyst and viral content strategist specializing in Instagram Reels.
# [... keep your full system prompt here ...]

# Retrieved Reels:
# {context}
# """
# system_prompt = """
# You are an expert social media performance analyst and viral content strategist specializing in Instagram Reels.

# Analyze the retrieved reels and return a JSON response. IMPORTANT: Every field marked as a list MUST be a JSON array, even if it contains only one item.

# Your response must be valid JSON with exactly these fields:
# - "analysis": {{"performance_drivers": ["string1", "string2"], "engagement_triggers": ["string1", "string2"]}}
# - "patterns": ["string1", "string2"]
# - "ideas": [{{"concept": "string", "hook": "string", "structure": ["step1", "step2", "step3"], "emotion": "string", "why_it_works": "string"}}]
# - "best_fit_recommendation": {{"best_idea_index": 0, "reason": "string"}}
# - "optimization_suggestion": {{"second_idea_emotional_variant": {{"change": "string", "add": "string", "result": "string"}}}}

# Retrieved Reels:
# {{context}}
# """
#old one
# system_prompt = """You are an expert viral content strategist for Instagram Reels. Analyze the retrieved reels and output ONLY a single-line minified JSON object with no newlines inside string values. Do not use \\n inside any string value. Keep all string values on one line.

# Required JSON structure (all strings must be single-line, no newlines):
# {{"analysis":{{"performance_drivers":["driver1","driver2"],"engagement_triggers":["trigger1","trigger2"]}},"patterns":["pattern1","pattern2"],"ideas":[{{"concept":"one line","hook":"one line","structure":["step1","step2","step3","step4","step5"],"emotion":"one word","why_it_works":"one line"}},{{"concept":"one line","hook":"one line","structure":["step1","step2","step3","step4","step5"],"emotion":"one word","why_it_works":"one line"}},{{"concept":"one line","hook":"one line","structure":["step1","step2","step3","step4","step5"],"emotion":"one word","why_it_works":"one line"}}],"best_fit_recommendation":{{"best_idea_index":0,"reason":"one line"}},"optimization_suggestion":{{"second_idea_emotional_variant":{{"change":"one line","add":"one line","result":"one line"}}}}}}

# Retrieved Reels: {context}"""
# prompt = ChatPromptTemplate.from_messages([
#     ("system", system_prompt),
#     MessagesPlaceholder("chat_history"),
#     ("human", "{input}"),
# ])
#new one
# system_prompt = """You are an expert viral content strategist for Instagram Reels. You help creators make viral content.

# You will receive REAL retrieved reels with captions, transcripts, likes, and handles.
# Analyze these specific reels and generate ideas with READY-TO-SHOOT scripts based on concrete details from them.
# Do NOT be generic. Use actual hooks, phrases, and patterns from the retrieved reels.

# Output ONLY a single-line minified JSON with no newlines inside strings.

# Required JSON structure:
# {{"analysis":{{"performance_drivers":["specific driver from reels","specific driver from reels"],"engagement_triggers":["specific trigger seen in reels","specific trigger seen in reels"]}},"patterns":["specific repeating pattern","another specific pattern"],"ideas":[{{"concept":"specific concept inspired by retrieved reels","hook":"exact scroll-stopping opening line the creator should say or show","structure":["scene 1","scene 2","scene 3","scene 4","scene 5"],"emotion":"one word","why_it_works":"specific reason tied to retrieved reel data","reference_url":"url of reel that inspired this","script":"FULL READY-TO-SHOOT SCRIPT: Hook: [exact opening line]. Scene 1: [what creator does and says]. Scene 2: [what creator does and says]. Scene 3: [what creator does and says]. CTA: [closing line or action]."}},{{"concept":"...","hook":"...","structure":["..."],"emotion":"...","why_it_works":"...","reference_url":"...","script":"FULL READY-TO-SHOOT SCRIPT: Hook: [...]. Scene 1: [...]. Scene 2: [...]. Scene 3: [...]. CTA: [...]"}},{{"concept":"...","hook":"...","structure":["..."],"emotion":"...","why_it_works":"...","reference_url":"...","script":"FULL READY-TO-SHOOT SCRIPT: Hook: [...]. Scene 1: [...]. Scene 2: [...]. Scene 3: [...]. CTA: [...]"}}],"best_fit_recommendation":{{"best_idea_index":0,"reason":"specific reason"}},"optimization_suggestion":{{"second_idea_emotional_variant":{{"change":"specific change","add":"specific addition","result":"expected outcome"}}}}}}

# Retrieved Reels:
# {context}"""
#new2
# system_prompt = """You are an expert social media performance analyst and viral content strategist specializing in Instagram Reels.
# You analyze high-performing reels to uncover repeatable success patterns and transform them into highly actionable viral content ideas.
# Focus on:
# - audience psychology  • scroll-stopping hooks  • retention mechanics
# - emotional triggers   • relatability & shareability
# Avoid generic observations.
# You are given REAL reels retrieved from a database. Each reel may include:
# - captions, transcript excerpts, engagement metrics, creator handles

# YOUR TASK
# 1. Analyze why the retrieved reels performed well (hook effectiveness, curiosity gaps, pacing, emotional triggers, novelty vs familiarity). Be specific and concise.
# 2. Identify repeating patterns (hook formats, storytelling flow, tone, pain points, visual framing, engagement triggers). Prioritize: repeatable, psychologically compelling, niche-relevant patterns.
# 3. Generate 3 HIGHLY SPECIFIC viral reel ideas from those patterns. Native to the niche, optimized for retention and shares. No generic trends.

# QUALITY RULES
# - Specific not generic • Optimize retention & shareability
# - Hooks must create curiosity gaps • Ideas must be immediately executable

# Respond with ONLY a valid JSON object — no markdown, no explanation, no extra text.

# {{"analysis":{{"performance_drivers":["...","..."],"engagement_triggers":["...","..."]}},"patterns":["...","..."],"ideas":[{{"concept":"...","hook":"...","structure":["step 1","step 2","step 3","step 4","step 5"],"emotion":"...","why_it_works":"...","reference_url":"..."}}],"best_fit_recommendation":{{"best_idea_index":0,"reason":"..."}},"optimization_suggestion":{{"second_idea_emotional_variant":{{"change":"...","add":"...","result":"..."}}}}}}

# Retrieved Reels:
# {context}"""
#new3
system_prompt = """You are an expert social media performance analyst and viral content strategist specializing in Instagram Reels.
You analyze high-performing reels to uncover repeatable success patterns and transform them into highly actionable viral content ideas.
Focus on:
- audience psychology  • scroll-stopping hooks  • retention mechanics
- emotional triggers   • relatability & shareability
Avoid generic observations.
You are given REAL reels retrieved from a database. Each reel may include:
- captions, transcript excerpts, engagement metrics, creator handles

YOUR TASK
1. Analyze why the retrieved reels performed well (hook effectiveness, curiosity gaps, pacing, emotional triggers, novelty vs familiarity). Be specific and concise.
2. Identify repeating patterns (hook formats, storytelling flow, tone, pain points, visual framing, engagement triggers). Prioritize: repeatable, psychologically compelling, niche-relevant patterns.
3. Generate 1 HIGHLY SPECIFIC viral reel idea from those patterns. Native to the niche, optimized for retention and shares. No generic trends.

QUALITY RULES
- Specific not generic • Optimize retention & shareability
- Hooks must create curiosity gaps • Ideas must be immediately executable
- structure must have exactly 5 scene descriptions


Respond with ONLY a valid JSON object — no markdown, no explanation, no extra text.

{{"analysis":{{"performance_drivers":["...","..."],"engagement_triggers":["...","..."]}},"patterns":["...","..."],"ideas":[{{"concept":"...","hook":"...","structure":["Scene 1: ...","Scene 2: ...","Scene 3: ...","Scene 4: ...","Scene 5: ..."],"emotion":"...","why_it_works":"...","reference_url":"..."}}],"best_fit_recommendation":{{"best_idea_index":0,"reason":"..."}},"optimization_suggestion":{{"second_idea_emotional_variant":{{"change":"...","add":"...","result":"..."}}}}}}

Retrieved Reels:
{context}"""
prompt = ChatPromptTemplate.from_messages([
    ("system", system_prompt),
    MessagesPlaceholder("chat_history"),
    ("human", "{input}"),
])
# ── Graph nodes ───────────────────────────────────────────────────────────────
def retrieve(state: AgentState) -> AgentState:
    """Node 1: fetch relevant reels"""
    results = query_reels(state["query"], top_k=8, text_weight=0.6)
    docs = []
    sources = []
    for r in results:
        meta = r["metadata"]
        reel = r.get("reel", {})
        docs.append(Document(
            page_content=(
                f"Owner: @{meta.get('owner','')}\n"
                f"Likes: {meta.get('likes', 0)}\n"
                f"Duration: {meta.get('duration', 0)}s\n"
                f"URL: {meta.get('video_url','')}\n"
                f"Caption: {meta.get('caption','')}\n"
                f"Transcript: {reel.get('transcript','')}"
            ).strip(),
            metadata={
                "id": r["id"], "owner": meta.get("owner", ""),
                "likes": meta.get("likes", 0), "duration": meta.get("duration", 0),
                "rrf_score": r.get("rrf_score", 0), "url": meta.get("video_url", ""),
            }
        ))
        sources.append(docs[-1].metadata)
    return {**state, "documents": docs, "sources": sources}


# def generate(state: AgentState) -> AgentState:
#     """Node 2: run LLM with retrieved context"""
#     context = "\n\n".join(d.page_content for d in state["documents"])
#     raw = llm.invoke(prompt.format_messages(
#         context=context,
#         chat_history=state["chat_history"],
#         input=state["query"]
#     ))
#     try:
#         text = raw.content.strip()
#         text = text.removeprefix("```json").removeprefix("```").removesuffix("```").strip()
#         parsed = StrategistOutput.model_validate_json(text)
#     except Exception as e:
#         print(f"[generate] Parse error: {e}")
#         parsed = None
#     return {**state, "answer": parsed}
# def generate(state: AgentState) -> AgentState:
#     context = "\n\n".join(d.page_content for d in state["documents"])
#     try:
#         parsed = llm.invoke(prompt.format_messages(
#             context=context,
#             chat_history=state["chat_history"],
#             input=state["query"]
#         ))
#     except Exception as e:
#         print(f"[generate] LLM error: {e}")
#         parsed = StrategistOutput(
#             analysis=AnalysisBlock(performance_drivers=[], engagement_triggers=[]),
#             patterns=[],
#             ideas=[],
#             best_fit_recommendation=BestFitRecommendation(
#                 best_idea_index=0, reason="Generation failed"
#             ),
#             optimization_suggestion=OptimizationSuggestion(
#                 second_idea_emotional_variant=OptimizationVariant(
#                     change="", add="", result=""
#                 )
#             )
#         )
#     return {**state, "answer": parsed}
def generate(state: AgentState) -> AgentState:
    context = "\n\n".join(d.page_content for d in state["documents"])
    
    _fallback = StrategistOutput(
        analysis=AnalysisBlock(performance_drivers=[], engagement_triggers=[]),
        patterns=[],
        ideas=[],
        best_fit_recommendation=BestFitRecommendation(
            best_idea_index=0, reason="Generation failed"
        ),
        optimization_suggestion=OptimizationSuggestion(
            second_idea_emotional_variant=OptimizationVariant(
                change="", add="", result=""
            )
        )
    )

    try:
        raw = llm.invoke(prompt.format_messages(
            context=context,
            chat_history=state["chat_history"],
            input=state["query"]
        ))
        text = raw.content.strip()
        
        # strip markdown fences if present
        text = text.removeprefix("```json").removeprefix("```").removesuffix("```").strip()
        
        # extract JSON object if model added extra text around it
        start = text.find("{")
        end = text.rfind("}") + 1
        if start == -1 or end == 0:
            print("[generate] No JSON object found in response")
            return {**state, "answer": _fallback}
        
        text = text[start:end]
        parsed = StrategistOutput.model_validate_json(text)

    except Exception as e:
        print(f"[generate] Error: {e}")
        return {**state, "answer": _fallback}

    return {**state, "answer": parsed}

def update_history(state: AgentState) -> AgentState:
    """Node 3: persist chat turn"""
    history = list(state["chat_history"])
    history.append(HumanMessage(content=state["query"]))
    answer_str = (
        state["answer"].model_dump_json(indent=2)
        if isinstance(state["answer"], StrategistOutput)
        else str(state["answer"])
    )
    history.append(AIMessage(content=answer_str))
    return {**state, "chat_history": history}


# ── Build graph ───────────────────────────────────────────────────────────────
workflow = StateGraph(AgentState)
workflow.add_node("retrieve", retrieve)
workflow.add_node("generate", generate)
workflow.add_node("update_history", update_history)

workflow.set_entry_point("retrieve")
workflow.add_edge("retrieve", "generate")
workflow.add_edge("generate", "update_history")
workflow.add_edge("update_history", END)

graph = workflow.compile()

# ── Shared chat history across turns ─────────────────────────────────────────
_chat_history: List = []

def run_optimized_pipeline(query: str) -> dict:
    global _chat_history
    result = graph.invoke({
        "query": query,
        "chat_history": _chat_history,
        "documents": [],
        "answer": None,
        "sources": []
    })
    _chat_history = result["chat_history"]
    return {
        "answer": result["answer"],
        "sources": result["sources"]   # routes.py expects "sources", not "context"
    }

def conversational_rag(query: str) -> dict:
    global _chat_history
    result = graph.invoke({
        "query": query,
        "chat_history": _chat_history,
        "documents": [],
        "answer": None,
        "sources": []
    })
    _chat_history = result["chat_history"]
    return {"answer": result["answer"], "context": result["documents"], "sources": result["sources"]}