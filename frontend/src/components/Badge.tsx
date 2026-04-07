import * as React from "react";

export function Badge({
  children,
  variant = "neutral",
}: {
  children: React.ReactNode;
  variant?: "neutral" | "good" | "warn" | "bad" | "info";
}) {
  const cls =
    variant === "good"
      ? "border-emerald-500/30 bg-emerald-500/10 text-emerald-200"
      : variant === "warn"
        ? "border-amber-500/30 bg-amber-500/10 text-amber-200"
        : variant === "bad"
          ? "border-rose-500/30 bg-rose-500/10 text-rose-200"
          : variant === "info"
            ? "border-cyan-500/30 bg-cyan-500/10 text-cyan-200"
            : "border-white/10 bg-white/5 text-white/80";

  return (
    <span
      className={[
        "inline-flex items-center rounded-full border px-2 py-0.5 text-[11px] font-medium tracking-wide",
        cls,
      ].join(" ")}
    >
      {children}
    </span>
  );
}

