import * as React from "react";

export function JsonPanel({
  title,
  jsonText,
}: {
  title: string;
  jsonText?: string | null;
}) {
  if (!jsonText) return null;
  let pretty = jsonText;
  try {
    pretty = JSON.stringify(JSON.parse(jsonText), null, 2);
  } catch {
    // keep as-is
  }
  return (
    <div className="rounded-xl border border-white/10 bg-black/20">
      <div className="px-3 py-2 text-[12px] font-medium text-white/80 border-b border-white/10">
        {title}
      </div>
      <pre className="max-h-64 overflow-auto p-3 text-[12px] leading-relaxed text-white/70">
        {pretty}
      </pre>
    </div>
  );
}

