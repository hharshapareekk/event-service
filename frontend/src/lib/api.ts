import type { Bundle, TimelineResponse, EventResponse } from "./types";

function apiBase() {
  return (
    process.env.NEXT_PUBLIC_API_BASE?.replace(/\/+$/, "") || "http://localhost:8080"
  );
}

async function apiGet<T>(path: string, init?: RequestInit): Promise<T> {
  const url = `${apiBase()}${path.startsWith("/") ? "" : "/"}${path}`;
  const res = await fetch(url, {
    ...init,
    headers: {
      Accept: "application/json",
      ...(init?.headers || {}),
    },
    cache: "no-store",
  });
  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`${res.status} ${res.statusText}${text ? ` — ${text}` : ""}`);
  }
  return (await res.json()) as T;
}

export function listBundles() {
  return apiGet<Bundle[]>("/bundles");
}

export function getTimeline(bundleId: number) {
  return apiGet<TimelineResponse>(`/timeline?bundleId=${bundleId}`);
}

export function searchEvents(params: {
  bundleId?: number;
  sourceType?: string;
  userName?: string;
  host?: string;
  srcIp?: string;
  minSeverity?: number;
  maxSeverity?: number;
  q?: string;
}) {
  const sp = new URLSearchParams();
  for (const [k, v] of Object.entries(params)) {
    if (v === undefined || v === null) continue;
    const s = String(v).trim();
    if (!s) continue;
    sp.set(k, s);
  }
  return apiGet<EventResponse[]>(`/events?${sp.toString()}`);
}

