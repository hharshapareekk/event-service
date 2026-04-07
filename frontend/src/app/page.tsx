/* eslint-disable @next/next/no-img-element */
"use client";

import * as React from "react";
import { GlassCard } from "@/components/GlassCard";
import { Badge } from "@/components/Badge";
import { EventRow } from "@/components/EventRow";
import { JsonPanel } from "@/components/JsonPanel";
import type { Bundle, EventResponse } from "@/lib/types";
import { getTimeline, listBundles, searchEvents } from "@/lib/api";

function clamp(n: number, a: number, b: number) {
  return Math.max(a, Math.min(b, n));
}

export default function HomePage() {
  const [bundles, setBundles] = React.useState<Bundle[]>([]);
  const [bundleId, setBundleId] = React.useState<number | null>(null);
  const [events, setEvents] = React.useState<EventResponse[]>([]);
  const [selectedId, setSelectedId] = React.useState<number | null>(null);
  const selected = React.useMemo(
    () => events.find((e) => e.id === selectedId) || null,
    [events, selectedId],
  );

  const [q, setQ] = React.useState("");
  const [sourceType, setSourceType] = React.useState("");
  const [minSeverity, setMinSeverity] = React.useState(0);
  const [maxSeverity, setMaxSeverity] = React.useState(7);
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);

  const sourceTypes = React.useMemo(() => {
    const set = new Set<string>();
    for (const e of events) if (e.sourceType) set.add(e.sourceType);
    return Array.from(set).sort();
  }, [events]);

  async function refreshBundles() {
    setError(null);
    setLoading(true);
    try {
      const b = await listBundles();
      b.sort((a, b) => (b.id ?? 0) - (a.id ?? 0));
      setBundles(b);
      if (bundleId == null && b.length) setBundleId(b[0].id);
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }

  async function loadTimeline(id: number) {
    setError(null);
    setLoading(true);
    try {
      const t = await getTimeline(id);
      setEvents(t.events || []);
      setSelectedId(t.events?.[0]?.id ?? null);
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }

  async function runSearch() {
    if (bundleId == null) return;
    setError(null);
    setLoading(true);
    try {
      const res = await searchEvents({
        bundleId,
        q: q || undefined,
        sourceType: sourceType || undefined,
        minSeverity,
        maxSeverity,
      });
      setEvents(res);
      setSelectedId(res?.[0]?.id ?? null);
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }

  React.useEffect(() => {
    refreshBundles();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  React.useEffect(() => {
    if (bundleId != null) loadTimeline(bundleId);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [bundleId]);

  return (
    <div className="min-h-full flex-1">
      <div className="relative mx-auto max-w-7xl px-4 py-6">
        <header className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
          <div className="flex items-start gap-3">
            <div className="mt-1 h-10 w-10 rounded-2xl border border-white/10 bg-white/[0.04] grid place-items-center">
              <span className="text-[12px] font-semibold tracking-wide text-white/70">
                ES
              </span>
            </div>
            <div>
              <div className="flex flex-wrap items-center gap-2">
                <h1 className="text-xl font-semibold tracking-tight text-white">
                  Event Service
                </h1>
                <Badge variant="info">timeline</Badge>
                <Badge variant="neutral">search</Badge>
              </div>
              <p className="mt-1 text-[13px] text-white/60 max-w-2xl">
                Unified incident timeline per bundle. Graph/correlation stays
                separate — this UI is for ordering, filtering, and inspection.
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <a
              className="text-[12px] text-white/60 hover:text-white"
              href={(process.env.NEXT_PUBLIC_API_BASE || "http://localhost:8080") + "/bundles"}
              target="_blank"
              rel="noreferrer"
            >
              API: /bundles
            </a>
            <button
              type="button"
              onClick={refreshBundles}
              className="rounded-xl border border-white/10 bg-white/[0.04] px-3 py-2 text-[12px] text-white hover:bg-white/[0.08]"
            >
              Refresh bundles
            </button>
          </div>
        </header>

        {error ? (
          <div className="mt-4 rounded-xl border border-rose-500/20 bg-rose-500/10 px-3 py-2 text-[12px] text-rose-100">
            {error}
          </div>
        ) : null}

        <div className="mt-6 grid grid-cols-1 gap-4 lg:grid-cols-12">
          <GlassCard className="lg:col-span-3">
            <div className="p-3 border-b border-white/10">
              <div className="text-[12px] font-medium text-white/80">
                Bundles
              </div>
              <div className="text-[11px] text-white/50">
                Select a bundle to load its timeline
              </div>
            </div>
            <div className="max-h-[70vh] overflow-auto p-2">
              {bundles.length === 0 ? (
                <div className="p-3 text-[12px] text-white/50">
                  {loading ? "Loading…" : "No bundles yet."}
                </div>
              ) : (
                <div className="space-y-2">
                  {bundles.map((b) => (
                    <button
                      key={b.id}
                      type="button"
                      onClick={() => setBundleId(b.id)}
                      className={[
                        "w-full text-left rounded-xl border px-3 py-2 transition",
                        bundleId === b.id
                          ? "border-cyan-400/40 bg-cyan-400/10"
                          : "border-white/10 bg-white/[0.02] hover:bg-white/[0.05] hover:border-white/20",
                      ].join(" ")}
                    >
                      <div className="flex items-center justify-between gap-2">
                        <div className="truncate text-[13px] font-medium text-white">
                          {b.name || b.bundleKey}
                        </div>
                        <div className="text-[11px] text-white/40">#{b.id}</div>
                      </div>
                      <div className="mt-1 truncate text-[11px] text-white/55">
                        {b.bundleKey}
                      </div>
                    </button>
                  ))}
                </div>
              )}
            </div>
          </GlassCard>

          <GlassCard className="lg:col-span-6">
            <div className="p-3 border-b border-white/10">
              <div className="flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
                <div>
                  <div className="text-[12px] font-medium text-white/80">
                    Timeline
                  </div>
                  <div className="text-[11px] text-white/50">
                    {bundleId == null
                      ? "Pick a bundle"
                      : `bundleId=${bundleId} • ${events.length} events`}
                  </div>
                </div>
                <div className="flex flex-wrap items-center gap-2">
                  <input
                    value={q}
                    onChange={(e) => setQ(e.target.value)}
                    placeholder="Search (message/action/object)…"
                    className="h-9 w-[220px] rounded-xl border border-white/10 bg-black/20 px-3 text-[12px] text-white placeholder:text-white/30 outline-none focus:border-cyan-400/40"
                  />
                  <select
                    value={sourceType}
                    onChange={(e) => setSourceType(e.target.value)}
                    className="h-9 rounded-xl border border-white/10 bg-black/20 px-3 text-[12px] text-white outline-none focus:border-cyan-400/40"
                  >
                    <option value="">All sources</option>
                    {sourceTypes.map((s) => (
                      <option key={s} value={s}>
                        {s}
                      </option>
                    ))}
                  </select>
                  <div className="flex items-center gap-2">
                    <span className="text-[11px] text-white/50">sev</span>
                    <input
                      type="number"
                      min={0}
                      max={7}
                      value={minSeverity}
                      onChange={(e) =>
                        setMinSeverity(clamp(Number(e.target.value), 0, 7))
                      }
                      className="h-9 w-14 rounded-xl border border-white/10 bg-black/20 px-2 text-[12px] text-white outline-none focus:border-cyan-400/40"
                    />
                    <span className="text-[11px] text-white/40">…</span>
                    <input
                      type="number"
                      min={0}
                      max={7}
                      value={maxSeverity}
                      onChange={(e) =>
                        setMaxSeverity(clamp(Number(e.target.value), 0, 7))
                      }
                      className="h-9 w-14 rounded-xl border border-white/10 bg-black/20 px-2 text-[12px] text-white outline-none focus:border-cyan-400/40"
                    />
                  </div>
                  <button
                    type="button"
                    onClick={runSearch}
                    className="h-9 rounded-xl border border-white/10 bg-white/[0.04] px-3 text-[12px] text-white hover:bg-white/[0.08]"
                    disabled={loading || bundleId == null}
                  >
                    {loading ? "Working…" : "Search"}
                  </button>
                  <button
                    type="button"
                    onClick={() => bundleId != null && loadTimeline(bundleId)}
                    className="h-9 rounded-xl border border-white/10 bg-white/[0.02] px-3 text-[12px] text-white/80 hover:bg-white/[0.06]"
                    disabled={loading || bundleId == null}
                  >
                    Reset
                  </button>
                </div>
              </div>
            </div>
            <div className="max-h-[70vh] overflow-auto p-2">
              {events.length === 0 ? (
                <div className="p-3 text-[12px] text-white/50">
                  {loading ? "Loading…" : "No events."}
                </div>
              ) : (
                <div className="space-y-2">
                  {events.map((e) => (
                    <EventRow
                      key={e.id}
                      e={e}
                      selected={e.id === selectedId}
                      onSelect={() => setSelectedId(e.id)}
                    />
                  ))}
                </div>
              )}
            </div>
          </GlassCard>

          <GlassCard className="lg:col-span-3">
            <div className="p-3 border-b border-white/10">
              <div className="text-[12px] font-medium text-white/80">
                Inspector
              </div>
              <div className="text-[11px] text-white/50">
                Click an event to view normalized fields + JSON blobs
              </div>
            </div>
            <div className="p-3 space-y-3 max-h-[70vh] overflow-auto">
              {selected ? (
                <>
                  <div className="rounded-xl border border-white/10 bg-black/20 p-3">
                    <div className="flex items-center justify-between">
                      <div className="text-[12px] font-medium text-white">
                        Event #{selected.id}
                      </div>
                      <Badge variant="info">{selected.sourceType || "?"}</Badge>
                    </div>
                    <div className="mt-2 space-y-1 text-[12px] text-white/70">
                      <div>
                        <span className="text-white/40">tsUtc</span>{" "}
                        {selected.tsUtc}
                      </div>
                      {selected.userName ? (
                        <div>
                          <span className="text-white/40">user</span>{" "}
                          {selected.userName}
                        </div>
                      ) : null}
                      {selected.host ? (
                        <div>
                          <span className="text-white/40">host</span>{" "}
                          {selected.host}
                        </div>
                      ) : null}
                      {selected.srcIp || selected.dstIp ? (
                        <div>
                          <span className="text-white/40">net</span>{" "}
                          {(selected.srcIp || "?") +
                            (selected.srcPort ? `:${selected.srcPort}` : "")}
                          {" → "}
                          {(selected.dstIp || "?") +
                            (selected.dstPort ? `:${selected.dstPort}` : "")}
                        </div>
                      ) : null}
                      {selected.action ? (
                        <div>
                          <span className="text-white/40">action</span>{" "}
                          {selected.action}
                        </div>
                      ) : null}
                      {selected.objectValue ? (
                        <div className="break-words">
                          <span className="text-white/40">object</span>{" "}
                          {selected.objectValue}
                        </div>
                      ) : null}
                      {selected.message ? (
                        <div className="break-words">
                          <span className="text-white/40">message</span>{" "}
                          {selected.message}
                        </div>
                      ) : null}
                    </div>
                  </div>

                  <JsonPanel title="metadataJson" jsonText={selected.metadataJson} />
                  <JsonPanel title="iocsJson" jsonText={selected.iocsJson} />
                  <JsonPanel
                    title="correlationKeysJson"
                    jsonText={selected.correlationKeysJson}
                  />
                  <JsonPanel title="rawRefJson" jsonText={selected.rawRefJson} />
                </>
              ) : (
                <div className="text-[12px] text-white/50">No selection.</div>
              )}
            </div>
          </GlassCard>
        </div>

        <footer className="mt-6 text-center text-[11px] text-white/40">
          Tip: set <code className="text-white/60">NEXT_PUBLIC_API_BASE</code>{" "}
          (e.g. <code className="text-white/60">http://localhost:8080</code>) if
          backend is on a different host/port.
        </footer>
      </div>
    </div>
  );
}
