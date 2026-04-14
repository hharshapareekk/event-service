package com.vulnuris.eventservice.config;

import com.vulnuris.eventservice.entity.Bundle;
import com.vulnuris.eventservice.entity.BundleMetadata;
import com.vulnuris.eventservice.entity.Event;
import com.vulnuris.eventservice.repository.BundleMetadataRepository;
import com.vulnuris.eventservice.repository.BundleRepository;
import com.vulnuris.eventservice.repository.EventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads the illustrative incident from the project brief when the DB is empty.
 * Replace or disable when real ingestion + correlation are wired in.
 */
@Configuration
@ConditionalOnProperty(name = "app.load-demo-data", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class SampleDataLoader {

    private final BundleRepository bundleRepository;
    private final EventRepository eventRepository;
    private final BundleMetadataRepository bundleMetadataRepository;

    @Bean
    CommandLineRunner loadDemoIncident() {
        return args -> {
            if (bundleRepository.count() > 0) {
                return;
            }
            ObjectMapper om = new ObjectMapper();

            // Bundle A: Original synthetic incident (kept for PDF-aligned demo).
            Bundle synthetic = bundleRepository.save(Bundle.builder()
                    .bundleKey("incident_bundle_001")
                    .name("Phishing → VPN → privilege escalation (sample)")
                    .description("Synthetic bundle aligned with Vulnuris brief §4.1")
                    .createdAt(LocalDateTime.now())
                    .build());

            bundleMetadataRepository.save(BundleMetadata.builder()
                    .bundle(synthetic)
                    .payloadJson("{\"timezone_hint\":\"mixed\",\"files\":[\"email_gateway.jsonl\",\"webserver_access.log\",\"firewall.csv\",\"vpn.csv\",\"idp_audit.jsonl\",\"windows_sec.jsonl\"]}")
                    .build());

            LocalDateTime tEmailUtc = ZonedDateTime.parse("2025-07-03T08:10:15+05:30")
                    .withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
            LocalDateTime tWeb = LocalDateTime.parse("2025-07-03T08:42:11");
            LocalDateTime tFw = LocalDateTime.parse("2025-07-03T08:44:05");
            LocalDateTime tVpn = LocalDateTime.parse("2025-07-03T08:47:19");
            LocalDateTime tIdp = LocalDateTime.parse("2025-07-03T08:48:02");
            LocalDateTime tWin = LocalDateTime.parse("2025-07-03T08:51:22");

            eventRepository.save(Event.builder()
                    .bundle(synthetic)
                    .tsUtc(tEmailUtc)
                    .tsOriginal("2025-07-03T08:10:15+05:30")
                    .tzOffset("+05:30")
                    .sourceType("email")
                    .host(null)
                    .userName("user@corp.com")
                    .srcIp(null)
                    .dstIp(null)
                    .action("delivered")
                    .objectValue("https://payroll-corp.com/update")
                    .result("success")
                    .severity(3)
                    .message("Email delivered: Payroll Update")
                    .iocsJson("{\"domains\":[\"payroll-corp.com\"],\"urls\":[\"https://payroll-corp.com/update\"]}")
                    .correlationKeysJson("{\"user\":\"user@corp.com\"}")
                    .rawRefJson("{\"file\":\"email_gateway_2025-07-03.jsonl\",\"offset\":1}")
                    .build());

            eventRepository.save(Event.builder()
                    .bundle(synthetic)
                    .tsUtc(tWeb)
                    .tsOriginal("03/Jul/2025:08:42:11 +0000")
                    .tzOffset("+00:00")
                    .sourceType("web")
                    .host("WEB01")
                    .userName("user@corp.com")
                    .srcIp("203.0.113.45")
                    .dstIp("10.0.2.5")
                    .action("get")
                    .objectValue("/login?user=user@corp.com")
                    .result("success")
                    .severity(2)
                    .message("GET /login?user=user@corp.com HTTP/1.1 200")
                    .correlationKeysJson("{\"user\":\"user@corp.com\",\"src_ip\":\"203.0.113.45\"}")
                    .rawRefJson("{\"file\":\"webserver_access_2025-07-03.log\",\"offset\":1}")
                    .build());

            eventRepository.save(Event.builder()
                    .bundle(synthetic)
                    .tsUtc(tFw)
                    .tsOriginal("2025-07-03T08:44:05Z")
                    .tzOffset("Z")
                    .sourceType("firewall")
                    .host(null)
                    .userName(null)
                    .srcIp("203.0.113.45")
                    .dstIp("10.0.2.5")
                    .protocol("TCP")
                    .dstPort(443)
                    .action("allow")
                    .objectValue("wan_to_web")
                    .result("success")
                    .severity(2)
                    .message("Firewall allow TCP/443")
                    .correlationKeysJson("{\"src_ip\":\"203.0.113.45\",\"dst_ip\":\"10.0.2.5\"}")
                    .rawRefJson("{\"file\":\"firewall_2025-07-03.csv\",\"offset\":2}")
                    .build());

            eventRepository.save(Event.builder()
                    .bundle(synthetic)
                    .tsUtc(tVpn)
                    .tsOriginal("2025-07-03T08:47:19Z")
                    .tzOffset("Z")
                    .sourceType("vpn")
                    .host(null)
                    .userName("user@corp.com")
                    .srcIp("203.0.113.45")
                    .action("login_success")
                    .objectValue("unknown")
                    .result("success")
                    .severity(4)
                    .message("VPN login_success")
                    .correlationKeysJson("{\"user\":\"user@corp.com\",\"src_ip\":\"203.0.113.45\"}")
                    .rawRefJson("{\"file\":\"vpn_2025-07-03.csv\",\"offset\":2}")
                    .build());

            eventRepository.save(Event.builder()
                    .bundle(synthetic)
                    .tsUtc(tIdp)
                    .tsOriginal("2025-07-03T08:48:02Z")
                    .tzOffset("Z")
                    .sourceType("idp")
                    .host(null)
                    .userName("user@corp.com")
                    .action("elevation")
                    .objectValue("admin-portal")
                    .result("success")
                    .severity(7)
                    .message("MFA-bypass privilege elevation")
                    .correlationKeysJson("{\"user\":\"user@corp.com\"}")
                    .rawRefJson("{\"file\":\"idp_audit_2025-07-03.jsonl\",\"offset\":1}")
                    .build());

            eventRepository.save(Event.builder()
                    .bundle(synthetic)
                    .tsUtc(tWin)
                    .tsOriginal("07/03/2025 08:51:22")
                    .tzOffset("assumed_UTC")
                    .sourceType("os")
                    .host("WEB01")
                    .userName("user@corp.com")
                    .action("policy_change")
                    .objectValue("Local policy")
                    .result("success")
                    .severity(6)
                    .message("Windows Security 4739: Policy change")
                    .correlationKeysJson("{\"user\":\"user@corp.com\",\"host\":\"WEB01\"}")
                    .rawRefJson("{\"file\":\"windows_sec_2025-07-03.evtx.jsonl\",\"offset\":1}")
                    .build());

            // Bundle B: Syslog sample you pasted (RFC5424-ish). Adds more realistic “multiple bundle” demo.
            Bundle syslogBundle = bundleRepository.save(Bundle.builder()
                    .bundleKey("bundle_syslog_2025-10-29")
                    .name("Linux syslog burst (sample)")
                    .description("Parsed from sample syslog lines (RFC5424).")
                    .createdAt(LocalDateTime.now())
                    .build());
            bundleMetadataRepository.save(BundleMetadata.builder()
                    .bundle(syslogBundle)
                    .payloadJson("{\"source\":\"syslog\",\"format\":\"rfc5424\",\"note\":\"seeded demo\"}")
                    .build());
            String[] syslogLines = new String[] {
                    "<76>1 2025-10-29T08:00:00.000+00:00 server-06 sudo 57511 - - Service postgresql started successfully",
                    "<35>1 2025-10-29T08:00:18.000+00:00 server-13 named 41546 - - Failed password for invalid user user9 from 115.90.55.229 port 26695 ssh2",
                    "<0>1 2025-10-29T08:00:36.000+00:00 server-08 sudo 44315 - - Connection from 113.129.176.67:22139 accepted",
                    "<8>1 2025-10-29T08:00:54.000+00:00 server-08 named 6936 - - User user6 logged out",
                    "<49>1 2025-10-29T08:01:12.000+00:00 server-14 httpd 8347 - - Configuration file reloaded",
                    "<50>1 2025-10-29T08:01:30.000+00:00 server-11 httpd 47229 - - Failed password for invalid user user5 from 87.162.152.66 port 32740 ssh2"
            };
            seedSyslog(syslogBundle, syslogLines);

            // Bundle C: Palo Alto-like firewall JSON (synthetic but matches common field names).
            Bundle paBundle = bundleRepository.save(Bundle.builder()
                    .bundleKey("bundle_paloalto_traffic_2025-10-29")
                    .name("Palo Alto traffic logs (sample)")
                    .description("Sample traffic events (JSON) seeded for UI demo.")
                    .createdAt(LocalDateTime.now())
                    .build());
            bundleMetadataRepository.save(BundleMetadata.builder()
                    .bundle(paBundle)
                    .payloadJson("{\"source\":\"paloalto\",\"format\":\"json\",\"note\":\"seeded demo\"}")
                    .build());
            String[] paJsonLines = new String[] {
                    "{\"receive_time\":\"2025-10-29T08:02:10Z\",\"type\":\"TRAFFIC\",\"subtype\":\"start\",\"src\":\"115.90.55.229\",\"dst\":\"10.0.2.5\",\"src_port\":52311,\"dst_port\":22,\"proto\":\"tcp\",\"app\":\"ssh\",\"action\":\"allow\",\"rule\":\"internet-to-dmz\",\"severity\":\"medium\"}",
                    "{\"receive_time\":\"2025-10-29T08:02:14Z\",\"type\":\"THREAT\",\"subtype\":\"spyware\",\"src\":\"203.0.113.45\",\"dst\":\"10.0.2.5\",\"src_port\":443,\"dst_port\":52512,\"proto\":\"tcp\",\"app\":\"ssl\",\"action\":\"alert\",\"rule\":\"dmz-egress\",\"severity\":\"high\",\"threat\":\"C2 Beacon\"}"
            };
            seedPaloAlto(paBundle, om, paJsonLines);

            // Bundle D: Web access log (common/combined-ish).
            Bundle webBundle = bundleRepository.save(Bundle.builder()
                    .bundleKey("bundle_web_access_2025-10-29")
                    .name("Web access logs (sample)")
                    .description("Sample access log lines seeded for UI demo.")
                    .createdAt(LocalDateTime.now())
                    .build());
            bundleMetadataRepository.save(BundleMetadata.builder()
                    .bundle(webBundle)
                    .payloadJson("{\"source\":\"web\",\"format\":\"common\",\"note\":\"seeded demo\"}")
                    .build());
            String[] accessLines = new String[] {
                    "115.90.55.229 - - [29/Oct/2025:08:03:01 +0000] \"GET /login HTTP/1.1\" 200 1243 \"-\" \"Mozilla/5.0\"",
                    "115.90.55.229 - user9 [29/Oct/2025:08:03:22 +0000] \"POST /api/auth HTTP/1.1\" 401 532 \"-\" \"Mozilla/5.0\"",
                    "203.0.113.45 - - [29/Oct/2025:08:03:57 +0000] \"GET /admin HTTP/1.1\" 403 321 \"-\" \"Mozilla/5.0\""
            };
            seedWebAccess(webBundle, accessLines);

            // Bundle E: Windows Security Events (based on sample CES Kafka format from teammate)
            Bundle winSecBundle = bundleRepository.save(Bundle.builder()
                    .bundleKey("bundle_winsec_2025-10-29")
                    .name("Windows Security Events (sample)")
                    .description("Windows Security event logs parsed from CES format — seeded for UI demo.")
                    .createdAt(LocalDateTime.now())
                    .build());
            bundleMetadataRepository.save(BundleMetadata.builder()
                    .bundle(winSecBundle)
                    .payloadJson("{\"source\":\"windows_security\",\"format\":\"ces_json\",\"note\":\"seeded demo\"}")
                    .build());
            seedWindowsSecurity(winSecBundle);
        };
    }

    private void seedWindowsSecurity(Bundle bundle) {
        Object[][] events = {
            {"2025-10-29 12:54:45", "WIN-SRV-01.contoso.local", "user7", "116.227.215.209", null, null, null, "windows_security", "TASK_CREATE", "event_4698", "SUCCESS", 5, "Scheduled task created by user7 — possible persistence mechanism"},
            {"2025-10-29 12:55:10", "WIN-SRV-01.contoso.local", "user7", "116.227.215.209", null, null, null, "windows_security", "LOGON", "event_4624", "SUCCESS", 3, "Interactive logon from external IP 116.227.215.209"},
            {"2025-10-29 12:56:02", "WIN-DC-02.contoso.local", "user3", "10.0.1.15", null, "10.0.1.1", 389, "windows_security", "KERBEROS_TGT", "event_4768", "SUCCESS", 2, "TGT request for user3 from domain controller"},
            {"2025-10-29 12:57:30", "WIN-SRV-01.contoso.local", "SYSTEM", null, null, null, null, "windows_security", "SERVICE_INSTALL", "event_4697", "SUCCESS", 6, "New service installed — possible lateral movement"},
            {"2025-10-29 12:58:15", "WIN-WK-05.contoso.local", "user12", "192.168.1.45", null, "10.0.2.5", 445, "windows_security", "LOGON_FAILED", "event_4625", "FAILURE", 4, "Failed logon attempt via SMB from internal workstation"},
            {"2025-10-29 12:59:00", "WIN-DC-02.contoso.local", "admin", "10.0.1.1", null, null, null, "windows_security", "POLICY_CHANGE", "event_4739", "SUCCESS", 7, "Domain policy modification — critical security change"},
            {"2025-10-29 13:00:45", "WIN-SRV-01.contoso.local", "user7", "116.227.215.209", null, "10.0.2.5", 3389, "windows_security", "RDP_CONNECT", "event_4624_type10", "SUCCESS", 5, "RDP connection from external IP — suspicious remote access"},
            {"2025-10-29 13:01:22", "WIN-SRV-01.contoso.local", "user7", null, null, null, null, "windows_security", "PRIV_ESCALATION", "event_4672", "SUCCESS", 6, "Special privileges assigned to new logon — admin token"},
            {"2025-10-29 13:02:10", "WIN-WK-05.contoso.local", "user12", "192.168.1.45", null, "10.0.2.5", 445, "windows_security", "LOGON_FAILED", "event_4625", "FAILURE", 4, "Second failed logon attempt — possible brute force"},
            {"2025-10-29 13:03:00", "WIN-SRV-03.contoso.local", "SYSTEM", null, null, null, null, "windows_security", "AUDIT_CLEAR", "event_1102", "SUCCESS", 7, "Security audit log cleared — anti-forensics detected"},
            {"2025-10-29 13:04:30", "WIN-DC-02.contoso.local", "user3", "10.0.1.15", null, "10.0.1.1", 389, "windows_security", "KERBEROS_SVC", "event_4769", "SUCCESS", 3, "Service ticket request — normal Kerberos activity"},
            {"2025-10-29 13:05:15", "WIN-SRV-01.contoso.local", "user7", "116.227.215.209", null, null, null, "windows_security", "PROCESS_CREATE", "event_4688", "SUCCESS", 5, "New process created: powershell.exe -enc [base64] — encoded command execution"},
            {"2025-10-29 13:06:00", "WIN-SRV-01.contoso.local", "user7", null, null, "203.0.113.50", 443, "windows_security", "OUTBOUND_CONN", "event_5156", "SUCCESS", 6, "Outbound connection to known C2 IP 203.0.113.50"},
            {"2025-10-29 13:07:45", "WIN-WK-05.contoso.local", "user12", "192.168.1.45", null, "10.0.2.5", 445, "windows_security", "LOGON", "event_4624", "SUCCESS", 3, "Successful logon after previous failures — account compromise likely"},
            {"2025-10-29 13:08:30", "WIN-SRV-01.contoso.local", "user7", null, null, null, null, "windows_security", "FILE_SHARE_ACCESS", "event_5140", "SUCCESS", 4, "Network share accessed: \\\\WIN-DC-02\\SYSVOL — possible data exfiltration"},
        };

        int offset = 0;
        for (Object[] row : events) {
            offset++;
            try {
                LocalDateTime tsUtc = LocalDateTime.parse((String) row[0], java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String host = (String) row[1];
                String user = (String) row[2];
                String srcIp = (String) row[3];
                Integer srcPort = (Integer) row[4];
                String dstIp = (String) row[5];
                Integer dstPort = (Integer) row[6];
                String sourceType = (String) row[7];
                String action = (String) row[8];
                String object = (String) row[9];
                String result = (String) row[10];
                int severity = (Integer) row[11];
                String message = (String) row[12];

                StringBuilder corrKeys = new StringBuilder("{");
                boolean first = true;
                if (user != null) { corrKeys.append("\"user\":\"").append(jsonEscape(user)).append("\""); first = false; }
                if (host != null) { if (!first) corrKeys.append(","); corrKeys.append("\"host\":\"").append(jsonEscape(host)).append("\""); first = false; }
                if (srcIp != null) { if (!first) corrKeys.append(","); corrKeys.append("\"src_ip\":\"").append(jsonEscape(srcIp)).append("\""); }
                corrKeys.append("}");

                eventRepository.save(Event.builder()
                        .bundle(bundle)
                        .tsUtc(tsUtc)
                        .tsOriginal((String) row[0])
                        .tzOffset("Z")
                        .sourceType(sourceType)
                        .host(host)
                        .userName(user)
                        .srcIp(srcIp)
                        .srcPort(srcPort)
                        .dstIp(dstIp)
                        .dstPort(dstPort)
                        .action(action)
                        .objectValue(object)
                        .result(result)
                        .severity(severity)
                        .message(message)
                        .correlationKeysJson(corrKeys.toString())
                        .rawRefJson("{\"source\":\"windows_security\",\"offset\":" + offset + "}")
                        .build());
            } catch (Exception ignored) {
                // best-effort demo seeding
            }
        }
    }

    private static final Pattern SYSLOG_PRI = Pattern.compile("^<(\\d+)>\\d+\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+(\\S+)\\s+-\\s+-\\s+(.*)$");
    private static final Pattern FAIL_PASS = Pattern.compile("Failed password for invalid user\\s+(\\S+)\\s+from\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+port\\s+(\\d+)\\s+(\\S+)");
    private static final Pattern CONN_ACCEPT = Pattern.compile("Connection from\\s+(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)\\s+accepted");
    private static final Pattern USER_LOGOUT = Pattern.compile("User\\s+(\\S+)\\s+logged out");
    private static final Pattern WEB_ACCESS = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\d+)\\s+\\S+\\s+(\\S+)\\s+\\[(\\d{2}/\\w{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2}\\s+[+-]\\d{4})\\]\\s+\"(\\S+)\\s+([^\\s]+)\\s+([^\"]+)\"\\s+(\\d{3})\\s+(\\d+).*$");

    private void seedSyslog(Bundle bundle, String[] lines) {
        int offset = 0;
        for (String line : lines) {
            offset++;
            Matcher m = SYSLOG_PRI.matcher(line);
            if (!m.matches()) {
                continue;
            }
            Integer pri = tryParseInt(m.group(1));
            String ts = m.group(2);
            String host = m.group(3);
            String app = m.group(4);
            String msg = m.group(6);

            int severity = 3;
            if (pri != null) {
                severity = Math.max(0, Math.min(7, pri % 8));
            }

            Event.EventBuilder b = Event.builder()
                    .bundle(bundle)
                    .tsUtc(OffsetDateTime.parse(ts).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime())
                    .tsOriginal(ts)
                    .tzOffset(offsetHintFromIso(ts))
                    .sourceType("syslog")
                    .host(host)
                    .action(app)
                    .message(msg)
                    .severity(severity)
                    .rawRefJson("{\"source\":\"syslog\",\"offset\":" + offset + "}");

            Matcher fail = FAIL_PASS.matcher(msg);
            if (fail.find()) {
                b.action("auth_failed")
                        .userName(fail.group(1))
                        .srcIp(fail.group(2))
                        .srcPort(tryParseInt(fail.group(3)))
                        .protocol(fail.group(4))
                        .result("failure")
                        .correlationKeysJson("{\"user\":\"" + jsonEscape(fail.group(1)) + "\",\"src_ip\":\"" + jsonEscape(fail.group(2)) + "\"}");
            } else {
                Matcher acc = CONN_ACCEPT.matcher(msg);
                if (acc.find()) {
                    b.action("connection_accepted")
                            .srcIp(acc.group(1))
                            .srcPort(tryParseInt(acc.group(2)))
                            .result("success")
                            .correlationKeysJson("{\"src_ip\":\"" + jsonEscape(acc.group(1)) + "\"}");
                } else {
                    Matcher lo = USER_LOGOUT.matcher(msg);
                    if (lo.find()) {
                        b.action("logout").userName(lo.group(1)).result("success")
                                .correlationKeysJson("{\"user\":\"" + jsonEscape(lo.group(1)) + "\",\"host\":\"" + jsonEscape(host) + "\"}");
                    } else {
                        b.result("success");
                    }
                }
            }

            eventRepository.save(b.build());
        }
    }

    private void seedPaloAlto(Bundle bundle, ObjectMapper om, String[] jsonLines) {
        int offset = 0;
        for (String line : jsonLines) {
            offset++;
            try {
                Map<String, Object> m = om.readValue(line, new TypeReference<>() {});
                String receiveTime = str(m.get("receive_time"));
                LocalDateTime tsUtc = receiveTime != null
                        ? OffsetDateTime.parse(receiveTime).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime()
                        : LocalDateTime.now(ZoneOffset.UTC);

                String severityStr = str(m.get("severity"));
                int sev = switch (severityStr == null ? "" : severityStr.toLowerCase()) {
                    case "critical" -> 7;
                    case "high" -> 6;
                    case "medium" -> 4;
                    case "low" -> 2;
                    default -> 3;
                };

                Event e = Event.builder()
                        .bundle(bundle)
                        .tsUtc(tsUtc)
                        .tsOriginal(receiveTime)
                        .tzOffset(receiveTime != null && receiveTime.endsWith("Z") ? "Z" : "+00:00")
                        .sourceType("firewall")
                        .host("PA-FW")
                        .srcIp(str(m.get("src")))
                        .dstIp(str(m.get("dst")))
                        .srcPort(intVal(m.get("src_port")))
                        .dstPort(intVal(m.get("dst_port")))
                        .protocol(str(m.get("proto")))
                        .action(str(m.get("action")))
                        .objectValue(str(m.get("rule")))
                        .result("allow".equalsIgnoreCase(str(m.get("action"))) ? "success" : "alert")
                        .severity(sev)
                        .message(buildPaloMessage(m))
                        .metadataJson(safeJson(om, m))
                        .correlationKeysJson(buildCorrKeys(str(m.get("src")), str(m.get("dst"))))
                        .rawRefJson("{\"source\":\"paloalto_json\",\"offset\":" + offset + "}")
                        .build();
                eventRepository.save(e);
            } catch (Exception ignored) {
                // best-effort demo seeding; skip malformed lines
            }
        }
    }

    private void seedWebAccess(Bundle bundle, String[] lines) {
        int offset = 0;
        for (String line : lines) {
            offset++;
            Matcher m = WEB_ACCESS.matcher(line);
            if (!m.matches()) {
                continue;
            }
            String srcIp = m.group(1);
            String user = "-".equals(m.group(2)) ? null : m.group(2);
            String ts = m.group(3);
            String method = m.group(4);
            String path = m.group(5);
            int status = Integer.parseInt(m.group(7));
            int bytes = Integer.parseInt(m.group(8));

            LocalDateTime tsUtc = parseApacheTsToUtc(ts);
            int sev = status >= 500 ? 6 : status >= 400 ? 5 : 2;

            eventRepository.save(Event.builder()
                    .bundle(bundle)
                    .tsUtc(tsUtc)
                    .tsOriginal(ts)
                    .tzOffset(ts.endsWith("+0000") ? "+00:00" : "unknown")
                    .sourceType("web")
                    .host("WEB01")
                    .userName(user)
                    .srcIp(srcIp)
                    .dstIp("10.0.2.5")
                    .action(method.toLowerCase())
                    .objectValue(path)
                    .result(status >= 400 ? "failure" : "success")
                    .severity(sev)
                    .message(method + " " + path + " → " + status + " (" + bytes + " bytes)")
                    .correlationKeysJson(buildCorrKeys(srcIp, "10.0.2.5"))
                    .rawRefJson("{\"source\":\"access_log\",\"offset\":" + offset + "}")
                    .build());
        }
    }

    private static LocalDateTime parseApacheTsToUtc(String tsWithOffset) {
        // "29/Oct/2025:08:03:01 +0000"
        // Avoid adding extra deps; parse with ZonedDateTime via a normalized ISO string.
        try {
            String[] parts = tsWithOffset.split(" ");
            String ts = parts[0]; // 29/Oct/2025:08:03:01
            String off = parts.length > 1 ? parts[1] : "+0000";
            String[] d = ts.split(":");
            String date = d[0]; // 29/Oct/2025
            String time = d[1] + ":" + d[2] + ":" + d[3];
            String[] dd = date.split("/");
            String day = dd[0];
            String mon = switch (dd[1]) {
                case "Jan" -> "01"; case "Feb" -> "02"; case "Mar" -> "03"; case "Apr" -> "04";
                case "May" -> "05"; case "Jun" -> "06"; case "Jul" -> "07"; case "Aug" -> "08";
                case "Sep" -> "09"; case "Oct" -> "10"; case "Nov" -> "11"; default -> "12";
            };
            String year = dd[2];
            String isoOff = off.substring(0, 3) + ":" + off.substring(3);
            return OffsetDateTime.parse(year + "-" + mon + "-" + day + "T" + time + isoOff)
                    .withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.now(ZoneOffset.UTC);
        }
    }

    private static String offsetHintFromIso(String iso) {
        if (iso == null) return null;
        if (iso.endsWith("Z")) return "Z";
        int plus = iso.lastIndexOf('+');
        int minus = iso.lastIndexOf('-');
        int idx = Math.max(plus, minus);
        if (idx > iso.indexOf('T')) return iso.substring(idx);
        return null;
    }

    private static Integer tryParseInt(String s) {
        try { return s == null ? null : Integer.parseInt(s); } catch (Exception e) { return null; }
    }

    private static Integer intVal(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.intValue();
        return tryParseInt(String.valueOf(o));
    }

    private static String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private static String buildPaloMessage(Map<String, Object> m) {
        String type = str(m.get("type"));
        String subtype = str(m.get("subtype"));
        String app = str(m.get("app"));
        String threat = str(m.get("threat"));
        if (threat != null && !threat.isBlank()) {
            return (type == null ? "FW" : type) + "/" + (subtype == null ? "-" : subtype) + " " + threat;
        }
        return (type == null ? "FW" : type) + "/" + (subtype == null ? "-" : subtype) + " app=" + (app == null ? "-" : app);
    }

    private static String buildCorrKeys(String src, String dst) {
        if (src == null && dst == null) return null;
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        if (src != null) { sb.append("\"src_ip\":\"").append(jsonEscape(src)).append("\""); first = false; }
        if (dst != null) {
            if (!first) sb.append(",");
            sb.append("\"dst_ip\":\"").append(jsonEscape(dst)).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    private static String safeJson(ObjectMapper om, Object o) {
        try { return om.writeValueAsString(o); } catch (Exception e) { return null; }
    }

    private static String jsonEscape(String s) {
        if (s == null) return null;
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
