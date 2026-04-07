import * as React from "react";
import type { EventResponse } from "@/lib/types";
import { Badge } from "./Badge";

function sevVariant(sev: number | null | undefined) {
  if (sev === null || sev === undefined) return "neutral" as const;
  if (sev >= 6) return "bad" as const;
  if (sev >= 5) return "warn" as const;
  if (sev >= 3) return "info" as const;
  return "good" as const;
}

export function EventRow({
  e,
  selected,
  onSelect,
}: {
  e: EventResponse;
  selected: boolean;
  onSelect: () => void;
}) {
  return (
    <button
      type="button"
      onClick={onSelect}
      className={[
        "w-full text-left rounded-xl border px-3 py-2 transition",
        selected
          ? "border-cyan-400/40 bg-cyan-400/10"
          : "border-white/10 bg-white/[0.02] hover:bg-white/[0.05] hover:border-white/20",
      ].join(" ")}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <div className="flex items-center gap-2">
            <span className="text-[12px] text-white/70">
              {new Date(e.tsUtc).toISOString().replace("T", " ").replace("Z", "Z")}
            </span>
            {e.sourceType ? <Badge variant="neutral">{e.sourceType}</Badge> : null}
            <Badge variant={sevVariant(e.severity ?? null)}>
              sev {e.severity ?? "?"}
            </Badge>
          </div>
          <div className="mt-1 truncate text-[13px] font-medium text-white">
            {e.message || `${e.action || "event"} ${e.objectValue || ""}`.trim()}
          </div>
          <div className="mt-1 flex flex-wrap gap-2 text-[12px] text-white/60">
            {e.userName ? <span>user={e.userName}</span> : null}
            {e.host ? <span>host={e.host}</span> : null}
            {e.srcIp ? <span>src={e.srcIp}</span> : null}
            {e.dstIp ? <span>dst={e.dstIp}</span> : null}
            {e.protocol || e.dstPort ? (
              <span>
                {e.protocol || "proto"}:{e.dstPort ?? "?"}
              </span>
            ) : null}
            {e.result ? <span>result={e.result}</span> : null}
          </div>
        </div>
        <div className="shrink-0 text-[11px] text-white/40">#{e.id}</div>
      </div>
    </button>
  );
}

