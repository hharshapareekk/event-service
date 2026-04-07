export type Bundle = {
  id: number;
  bundleKey: string;
  name?: string | null;
  description?: string | null;
  createdAt?: string | null;
};

export type EventResponse = {
  id: number;
  bundleId: number;
  tsUtc: string;
  tsOriginal?: string | null;
  tzOffset?: string | null;
  sourceType?: string | null;
  host?: string | null;
  userName?: string | null;
  srcIp?: string | null;
  dstIp?: string | null;
  srcPort?: number | null;
  dstPort?: number | null;
  protocol?: string | null;
  action?: string | null;
  objectValue?: string | null;
  result?: string | null;
  severity?: number | null;
  message?: string | null;
  metadataJson?: string | null;
  iocsJson?: string | null;
  correlationKeysJson?: string | null;
  rawRefJson?: string | null;
};

export type TimelineResponse = {
  bundleId: number;
  bundleKey?: string | null;
  events: EventResponse[];
  stats?: Record<string, unknown>;
  rangeStartUtc?: string | null;
  rangeEndUtc?: string | null;
};

