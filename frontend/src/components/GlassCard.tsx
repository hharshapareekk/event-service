import * as React from "react";

export function GlassCard({
  children,
  className = "",
}: {
  children: React.ReactNode;
  className?: string;
}) {
  return (
    <div
      className={[
        "rounded-2xl border border-white/10 bg-white/[0.03] backdrop-blur-xl shadow-[0_1px_0_0_rgba(255,255,255,0.07)_inset] overflow-hidden",
        className,
      ].join(" ")}
    >
      {children}
    </div>
  );
}

