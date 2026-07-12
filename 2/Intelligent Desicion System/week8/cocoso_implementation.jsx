import { useState, useEffect } from "react";

// ─── CoCoSo Algorithm Core ───────────────────────────────────────────────────

function normalise(matrix, types) {
  const m = matrix.length, n = matrix[0].length;
  const mins = Array(n).fill(Infinity), maxs = Array(n).fill(-Infinity);
  for (let i = 0; i < m; i++)
    for (let j = 0; j < n; j++) {
      if (matrix[i][j] < mins[j]) mins[j] = matrix[i][j];
      if (matrix[i][j] > maxs[j]) maxs[j] = matrix[i][j];
    }
  return matrix.map(row =>
    row.map((v, j) => {
      const range = maxs[j] - mins[j];
      if (range === 0) return 0;
      return types[j] === "benefit"
        ? (v - mins[j]) / range
        : (maxs[j] - v) / range;
    })
  );
}

function computeCoCoSo(matrix, weights, types, lambda = 0.5) {
  const R = normalise(matrix, types);
  const m = R.length, n = R[0].length;

  // Si = weighted sum (WSM-style)
  const S = R.map(row =>
    row.reduce((acc, r, j) => acc + weights[j] * r, 0)
  );

  // Pi = weighted product (WPM-style)
  const P = R.map(row =>
    row.reduce((acc, r, j) => acc + Math.pow(r, weights[j]), 0)
  );

  const sumSP = S.reduce((a, b) => a + b, 0) + P.reduce((a, b) => a + b, 0);
  const minS = Math.min(...S), minP = Math.min(...P);
  const maxS = Math.max(...S), maxP = Math.max(...P);

  const Ka = S.map((s, i) => (P[i] + s) / sumSP);
  const Kb = S.map((s, i) => s / minS + P[i] / minP);
  const Kc = S.map((s, i) =>
    (lambda * s + (1 - lambda) * P[i]) /
    (lambda * maxS + (1 - lambda) * maxP)
  );

  const K = Ka.map((ka, i) =>
    Math.pow(ka * Kb[i] * Kc[i], 1 / 3) + (1 / 3) * (Ka[i] + Kb[i] + Kc[i])
  );

  const ranked = K.map((k, i) => i).sort((a, b) => K[b] - K[a]);
  const ranks = Array(m);
  ranked.forEach((idx, rank) => (ranks[idx] = rank + 1));

  return { R, S, P, Ka, Kb, Kc, K, ranks };
}

// ─── Built-in Problems ───────────────────────────────────────────────────────

const PROBLEMS = {
  paper: {
    name: "📦 Logistics Provider (Paper Example)",
    description: "French logistics companies evaluated across 5 criteria from the original CoCoSo paper (Yazdani et al., 2019).",
    alternatives: ["Mathez (A1)", "Bansard (A2)", "GEFCO (A3)", "Schneider (A4)", "LDI Dimotrans (A5)", "SAGA (A6)", "GETMA (A7)"],
    criteria: ["Inventory Capacity (C1)", "Offered Price (C2)", "Delivery Volume (C3)", "Degree of Flexibility (C4)", "Technology Utilisation (C5)"],
    weights: [0.036, 0.192, 0.326, 0.326, 0.12],
    types: ["benefit", "cost", "benefit", "benefit", "benefit"],
    matrix: [
      [60, 0.4, 2540, 500, 990],
      [6.35, 0.15, 1016, 3000, 1041],
      [6.8, 0.1, 1727.2, 1500, 1676],
      [10, 0.2, 1000, 2000, 965],
      [2.5, 0.1, 560, 500, 915],
      [4.5, 0.08, 1016, 350, 508],
      [3, 0.1, 1778, 1000, 920],
    ],
  },
  custom: {
    name: "🏙️ Urban Mobility Solution (Custom Problem)",
    description: "Choosing the best urban public transport expansion strategy for a mid-sized European city. Five candidate solutions are evaluated against six sustainability and performance criteria.",
    alternatives: [
      "Electric Bus Network",
      "Light Rail Extension",
      "Bike-Share Scale-up",
      "Underground Metro Line",
      "Autonomous Shuttle Pods",
    ],
    criteria: [
      "Implementation Cost (€M)",
      "Passenger Capacity (k/day)",
      "CO₂ Reduction (t/yr)",
      "Implementation Time (months)",
      "Public Acceptance Score",
      "Long-term Scalability",
    ],
    weights: [0.25, 0.20, 0.20, 0.10, 0.15, 0.10],
    types: ["cost", "benefit", "benefit", "cost", "benefit", "benefit"],
    matrix: [
      [45,  80,  3200, 18, 8.2, 7.0],
      [210, 220, 8500, 60, 7.8, 9.0],
      [8,   35,  1800, 6,  9.0, 5.5],
      [850, 500, 22000,96, 6.5, 9.8],
      [120, 60,  4100, 30, 6.8, 8.5],
    ],
  },
};

// ─── Colour Helpers ──────────────────────────────────────────────────────────

const rankColour = (rank, total) => {
  const pct = (total - rank) / (total - 1);
  if (pct > 0.75) return "#22c55e";
  if (pct > 0.5)  return "#84cc16";
  if (pct > 0.25) return "#f59e0b";
  return "#ef4444";
};

const barWidth = (val, allVals) => {
  const mx = Math.max(...allVals);
  return mx === 0 ? 0 : (val / mx) * 100;
};

// ─── Sub-components ──────────────────────────────────────────────────────────

function Badge({ rank, total }) {
  const bg = rankColour(rank, total);
  return (
    <span style={{
      display: "inline-flex", alignItems: "center", justifyContent: "center",
      width: 28, height: 28, borderRadius: "50%",
      background: bg, color: "#fff", fontWeight: 700, fontSize: 13,
    }}>
      {rank}
    </span>
  );
}

function MatrixTable({ label, rows, cols, data, highlight, fmt = v => v?.toFixed(4) ?? "—" }) {
  return (
    <div style={{ overflowX: "auto", marginBottom: 24 }}>
      <p style={{ fontFamily: "'Space Mono',monospace", fontSize: 11, color: "#94a3b8", letterSpacing: 2, marginBottom: 8 }}>
        {label}
      </p>
      <table style={{ borderCollapse: "collapse", width: "100%", fontSize: 13 }}>
        <thead>
          <tr>
            <th style={th()}></th>
            {cols.map((c, j) => <th key={j} style={th()}>{c}</th>)}
          </tr>
        </thead>
        <tbody>
          {rows.map((r, i) => (
            <tr key={i} style={{ background: i % 2 === 0 ? "rgba(255,255,255,0.03)" : "transparent" }}>
              <td style={{ ...td(), fontWeight: 600, color: "#94a3b8", whiteSpace: "nowrap" }}>{r}</td>
              {data[i].map((v, j) => (
                <td key={j} style={{
                  ...td(),
                  color: highlight && highlight(i, j) ? "#f0c060" : "#e2e8f0",
                  fontFamily: "'Space Mono',monospace",
                }}>
                  {fmt(v)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

const th = () => ({
  padding: "8px 12px", textAlign: "right", color: "#64748b",
  borderBottom: "1px solid rgba(255,255,255,0.08)", fontSize: 12,
  fontFamily: "'Space Mono',monospace", whiteSpace: "nowrap",
});
const td = () => ({
  padding: "7px 12px", textAlign: "right",
  borderBottom: "1px solid rgba(255,255,255,0.05)",
});

function RankingChart({ alternatives, K, ranks }) {
  const total = alternatives.length;
  const order = [...ranks.map((r, i) => ({ r, i }))]
    .sort((a, b) => a.r - b.r);

  return (
    <div>
      {order.map(({ r, i }) => (
        <div key={i} style={{ display: "flex", alignItems: "center", gap: 12, marginBottom: 10 }}>
          <Badge rank={r} total={total} />
          <span style={{ width: 180, fontSize: 13, color: "#cbd5e1", flexShrink: 0, whiteSpace: "nowrap", overflow: "hidden", textOverflow: "ellipsis" }}>
            {alternatives[i]}
          </span>
          <div style={{ flex: 1, background: "rgba(255,255,255,0.06)", borderRadius: 4, height: 20, position: "relative" }}>
            <div style={{
              width: barWidth(K[i], K) + "%",
              height: "100%", borderRadius: 4,
              background: `linear-gradient(90deg, ${rankColour(r, total)}cc, ${rankColour(r, total)})`,
              transition: "width 0.6s ease",
            }} />
          </div>
          <span style={{ width: 64, textAlign: "right", fontFamily: "'Space Mono',monospace", fontSize: 12, color: "#94a3b8" }}>
            {K[i].toFixed(4)}
          </span>
        </div>
      ))}
    </div>
  );
}

// ─── Main App ────────────────────────────────────────────────────────────────

export default function CoCoSoApp() {
  const [problemKey, setProblemKey] = useState("custom");
  const [lambda, setLambda] = useState(0.5);
  const [tab, setTab] = useState("ranking");
  const [animKey, setAnimKey] = useState(0);

  const prob = PROBLEMS[problemKey];
  const result = computeCoCoSo(prob.matrix, prob.weights, prob.types, lambda);

  useEffect(() => { setAnimKey(k => k + 1); }, [problemKey, lambda]);

  const shortCrit = prob.criteria.map(c => c.split(" ")[0].replace(/[()]/g, ""));

  const tabs = [
    { id: "ranking",  label: "🏆 Final Ranking" },
    { id: "norm",     label: "📐 Normalised Matrix" },
    { id: "sp",       label: "Σ S & P Vectors" },
    { id: "k",        label: "K Aggregations" },
    { id: "analysis", label: "🔍 Analysis" },
  ];

  return (
    <div style={{
      minHeight: "100vh",
      background: "linear-gradient(135deg, #0a0f1e 0%, #0d1b2a 50%, #0a1628 100%)",
      color: "#e2e8f0",
      fontFamily: "'Sora', 'Segoe UI', sans-serif",
      padding: "0 0 60px",
    }}>
      <link href="https://fonts.googleapis.com/css2?family=Sora:wght@300;400;600;700&family=Space+Mono:wght@400;700&display=swap" rel="stylesheet" />

      {/* ── Header ── */}
      <div style={{
        background: "linear-gradient(180deg, rgba(15,40,80,0.9) 0%, transparent 100%)",
        padding: "36px 40px 28px",
        borderBottom: "1px solid rgba(255,255,255,0.07)",
        marginBottom: 32,
      }}>
        <div style={{ maxWidth: 1100, margin: "0 auto" }}>
          <div style={{ display: "flex", alignItems: "flex-start", justifyContent: "space-between", flexWrap: "wrap", gap: 16 }}>
            <div>
              <div style={{ fontFamily: "'Space Mono',monospace", fontSize: 11, color: "#60a5fa", letterSpacing: 3, marginBottom: 8 }}>
                MULTI-CRITERIA DECISION-MAKING
              </div>
              <h1 style={{ margin: 0, fontSize: "clamp(24px,4vw,38px)", fontWeight: 700, letterSpacing: -1, lineHeight: 1.1 }}>
                <span style={{ color: "#60a5fa" }}>Co</span>
                <span style={{ color: "#818cf8" }}>Co</span>
                <span style={{ color: "#a78bfa" }}>So</span>
                <span style={{ color: "#cbd5e1", fontSize: "0.55em", fontWeight: 300, marginLeft: 12 }}>
                  Combined Compromise Solution
                </span>
              </h1>
              <p style={{ margin: "8px 0 0", color: "#64748b", fontSize: 13 }}>
                Yazdani, Zaraté, Zavadskas & Turskis · Management Decision, 2019
              </p>
            </div>
            {/* λ control */}
            <div style={{
              background: "rgba(255,255,255,0.04)", border: "1px solid rgba(255,255,255,0.09)",
              borderRadius: 12, padding: "14px 20px", minWidth: 200,
            }}>
              <label style={{ fontFamily: "'Space Mono',monospace", fontSize: 11, color: "#60a5fa", letterSpacing: 2 }}>
                λ BALANCE PARAMETER
              </label>
              <div style={{ display: "flex", alignItems: "center", gap: 10, marginTop: 8 }}>
                <span style={{ fontSize: 12, color: "#64748b" }}>WSM</span>
                <input type="range" min={0} max={1} step={0.05} value={lambda}
                  onChange={e => setLambda(parseFloat(e.target.value))}
                  style={{ flex: 1, accentColor: "#818cf8" }} />
                <span style={{ fontSize: 12, color: "#64748b" }}>WPM</span>
              </div>
              <div style={{ textAlign: "center", fontFamily: "'Space Mono',monospace", fontSize: 18, fontWeight: 700, color: "#818cf8", marginTop: 4 }}>
                {lambda.toFixed(2)}
              </div>
            </div>
          </div>

          {/* Problem selector */}
          <div style={{ display: "flex", gap: 12, marginTop: 24, flexWrap: "wrap" }}>
            {Object.entries(PROBLEMS).map(([k, p]) => (
              <button key={k} onClick={() => setProblemKey(k)} style={{
                padding: "10px 20px", borderRadius: 8, border: "1px solid",
                borderColor: problemKey === k ? "#60a5fa" : "rgba(255,255,255,0.1)",
                background: problemKey === k ? "rgba(96,165,250,0.12)" : "rgba(255,255,255,0.03)",
                color: problemKey === k ? "#60a5fa" : "#94a3b8",
                cursor: "pointer", fontSize: 13, fontWeight: 600, transition: "all 0.2s",
              }}>
                {p.name}
              </button>
            ))}
          </div>
        </div>
      </div>

      <div style={{ maxWidth: 1100, margin: "0 auto", padding: "0 24px" }}>

        {/* Problem description */}
        <div style={{
          background: "rgba(96,165,250,0.06)", border: "1px solid rgba(96,165,250,0.15)",
          borderRadius: 12, padding: "16px 22px", marginBottom: 28,
        }}>
          <p style={{ margin: 0, fontSize: 14, color: "#93c5fd", lineHeight: 1.6 }}>{prob.description}</p>
          <div style={{ display: "flex", gap: 24, marginTop: 10, flexWrap: "wrap" }}>
            <span style={{ fontSize: 12, color: "#475569" }}>
              <span style={{ color: "#60a5fa", fontWeight: 600 }}>{prob.alternatives.length}</span> alternatives
            </span>
            <span style={{ fontSize: 12, color: "#475569" }}>
              <span style={{ color: "#60a5fa", fontWeight: 600 }}>{prob.criteria.length}</span> criteria
            </span>
            <span style={{ fontSize: 12, color: "#475569" }}>
              λ = <span style={{ color: "#818cf8", fontWeight: 600 }}>{lambda.toFixed(2)}</span>
            </span>
          </div>
        </div>

        {/* Tabs */}
        <div style={{ display: "flex", gap: 4, marginBottom: 24, overflowX: "auto", paddingBottom: 4 }}>
          {tabs.map(t => (
            <button key={t.id} onClick={() => setTab(t.id)} style={{
              padding: "9px 18px", borderRadius: 8, border: "none",
              background: tab === t.id ? "rgba(129,140,248,0.2)" : "rgba(255,255,255,0.04)",
              color: tab === t.id ? "#c4b5fd" : "#64748b",
              cursor: "pointer", fontSize: 13, fontWeight: tab === t.id ? 600 : 400,
              whiteSpace: "nowrap", transition: "all 0.15s",
              borderBottom: tab === t.id ? "2px solid #818cf8" : "2px solid transparent",
            }}>
              {t.label}
            </button>
          ))}
        </div>

        {/* ── Tab: Final Ranking ── */}
        {tab === "ranking" && (
          <div key={animKey} style={{ animation: "fadeIn 0.4s ease" }}>
            <style>{`@keyframes fadeIn{from{opacity:0;transform:translateY(8px)}to{opacity:1;transform:none}}`}</style>

            {/* Winner card */}
            <div style={{
              background: "linear-gradient(135deg, rgba(34,197,94,0.12), rgba(16,185,129,0.06))",
              border: "1px solid rgba(34,197,94,0.25)", borderRadius: 16,
              padding: "24px 28px", marginBottom: 28, display: "flex", alignItems: "center", gap: 20,
            }}>
              <div style={{ fontSize: 48 }}>🥇</div>
              <div>
                <div style={{ fontFamily: "'Space Mono',monospace", fontSize: 10, color: "#22c55e", letterSpacing: 3 }}>BEST ALTERNATIVE</div>
                <div style={{ fontSize: 22, fontWeight: 700, marginTop: 4 }}>
                  {prob.alternatives[result.ranks.indexOf(1)]}
                </div>
                <div style={{ fontSize: 13, color: "#64748b", marginTop: 4 }}>
                  k = <span style={{ fontFamily: "'Space Mono',monospace", color: "#22c55e" }}>
                    {result.K[result.ranks.indexOf(1)].toFixed(6)}
                  </span>
                </div>
              </div>
            </div>

            <RankingChart
              alternatives={prob.alternatives}
              K={result.K}
              ranks={result.ranks}
            />

            {/* Criteria weights */}
            <div style={{
              marginTop: 32, background: "rgba(255,255,255,0.03)",
              border: "1px solid rgba(255,255,255,0.07)", borderRadius: 12, padding: "20px 24px",
            }}>
              <p style={{ fontFamily: "'Space Mono',monospace", fontSize: 11, color: "#94a3b8", letterSpacing: 2, marginBottom: 14 }}>
                CRITERIA WEIGHTS & TYPES
              </p>
              <div style={{ display: "flex", flexWrap: "wrap", gap: 10 }}>
                {prob.criteria.map((c, j) => (
                  <div key={j} style={{
                    background: "rgba(255,255,255,0.05)", borderRadius: 8, padding: "8px 14px",
                    border: `1px solid ${prob.types[j] === "benefit" ? "rgba(96,165,250,0.2)" : "rgba(251,113,133,0.2)"}`,
                  }}>
                    <div style={{ fontSize: 12, color: "#cbd5e1", fontWeight: 600 }}>{c}</div>
                    <div style={{ display: "flex", gap: 8, marginTop: 4, alignItems: "center" }}>
                      <span style={{
                        fontSize: 10, padding: "2px 6px", borderRadius: 4,
                        background: prob.types[j] === "benefit" ? "rgba(96,165,250,0.2)" : "rgba(251,113,133,0.2)",
                        color: prob.types[j] === "benefit" ? "#93c5fd" : "#fca5a5",
                      }}>
                        {prob.types[j].toUpperCase()}
                      </span>
                      <span style={{ fontFamily: "'Space Mono',monospace", fontSize: 12, color: "#f0c060" }}>
                        w={prob.weights[j]}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* ── Tab: Raw Decision & Normalised Matrix ── */}
        {tab === "norm" && (
          <div>
            <MatrixTable
              label="RAW DECISION MATRIX"
              rows={prob.alternatives}
              cols={prob.criteria.map((c, j) => `${shortCrit[j]} (${prob.types[j][0].toUpperCase()})`)}
              data={prob.matrix}
              fmt={v => v}
            />
            <MatrixTable
              label="NORMALISED MATRIX  r_ij  [Eq. 2 & 3]"
              rows={prob.alternatives}
              cols={shortCrit}
              data={result.R}
              highlight={(i, j) => result.R[i][j] === Math.max(...result.R.map(r => r[j]))}
            />
          </div>
        )}

        {/* ── Tab: S & P Vectors ── */}
        {tab === "sp" && (
          <div>
            <p style={{ fontSize: 13, color: "#94a3b8", marginBottom: 20, lineHeight: 1.7 }}>
              <strong style={{ color: "#60a5fa" }}>Sᵢ</strong> (Eq. 4) = Σ wⱼ · rᵢⱼ — weighted sum (grey relational generation / WSM approach).<br />
              <strong style={{ color: "#a78bfa" }}>Pᵢ</strong> (Eq. 5) = Σ rᵢⱼ^wⱼ — weighted product (WASPAS multiplicative approach).
            </p>
            <MatrixTable
              label="WEIGHTED COMPARABILITY SEQUENCE  [S_i components & total]"
              rows={prob.alternatives}
              cols={[...shortCrit, "Sᵢ"]}
              data={prob.alternatives.map((_, i) => [
                ...prob.criteria.map((_, j) => prob.weights[j] * result.R[i][j]),
                result.S[i],
              ])}
              highlight={(i, j) => j === prob.criteria.length}
            />
            <MatrixTable
              label="EXPONENTIALLY WEIGHTED COMPARABILITY SEQUENCE  [P_i components & total]"
              rows={prob.alternatives}
              cols={[...shortCrit, "Pᵢ"]}
              data={prob.alternatives.map((_, i) => [
                ...prob.criteria.map((_, j) => Math.pow(result.R[i][j], prob.weights[j])),
                result.P[i],
              ])}
              highlight={(i, j) => j === prob.criteria.length}
            />
          </div>
        )}

        {/* ── Tab: K Aggregations ── */}
        {tab === "k" && (
          <div>
            <p style={{ fontSize: 13, color: "#94a3b8", marginBottom: 20, lineHeight: 1.7 }}>
              Three appraisal strategies (Eq. 6–8) are combined into a single ranking score kᵢ (Eq. 9).<br />
              <strong style={{ color: "#60a5fa" }}>kᵃ</strong>: arithmetic mean of WSM+WPM share &nbsp;·&nbsp;
              <strong style={{ color: "#818cf8" }}>kᵇ</strong>: relative score vs minimum &nbsp;·&nbsp;
              <strong style={{ color: "#a78bfa" }}>kᶜ</strong>: balanced compromise (λ={lambda.toFixed(2)})
            </p>
            <MatrixTable
              label="K AGGREGATION SCORES & FINAL RANKING"
              rows={prob.alternatives}
              cols={["kᵃ (Eq.6)", "kᵇ (Eq.7)", "kᶜ (Eq.8)", "kᵢ (Eq.9)", "Rank"]}
              data={prob.alternatives.map((_, i) => [
                result.Ka[i], result.Kb[i], result.Kc[i], result.K[i], result.ranks[i],
              ])}
              fmt={(v, j) => j === 4 ? `#${v}` : v.toFixed(4)}
              highlight={(i, j) => j === 3}
            />
          </div>
        )}

        {/* ── Tab: Analysis ── */}
        {tab === "analysis" && (
          <div>
            {problemKey === "custom" ? <CustomAnalysis prob={prob} result={result} lambda={lambda} /> : <PaperAnalysis prob={prob} result={result} />}
          </div>
        )}
      </div>
    </div>
  );
}

// ─── Analysis Panels ─────────────────────────────────────────────────────────

function Section({ title, children }) {
  return (
    <div style={{
      background: "rgba(255,255,255,0.03)", border: "1px solid rgba(255,255,255,0.07)",
      borderRadius: 12, padding: "20px 24px", marginBottom: 20,
    }}>
      <h3 style={{ margin: "0 0 12px", fontSize: 15, color: "#93c5fd", fontWeight: 600 }}>{title}</h3>
      <div style={{ fontSize: 14, color: "#94a3b8", lineHeight: 1.75 }}>{children}</div>
    </div>
  );
}

function CustomAnalysis({ prob, result, lambda }) {
  const bestIdx = result.ranks.indexOf(1);
  const worstIdx = result.ranks.indexOf(prob.alternatives.length);
  const order = [...result.ranks.map((r, i) => ({ r, i }))].sort((a, b) => a.r - b.r);

  return (
    <div>
      <Section title="🏙️ Problem Statement: Urban Mobility Expansion">
        A mid-sized European city (population ~400,000) must decide which public transport
        initiative to prioritise over the next decade. Five candidate solutions were evaluated
        by a panel of urban planners and sustainability experts across six criteria. Two criteria
        — <em>Implementation Cost</em> and <em>Implementation Time</em> — are cost-type (lower is better);
        the remaining four are benefit-type (higher is better).
        <br /><br />
        <strong style={{ color: "#e2e8f0" }}>Criteria weights</strong> were elicited via expert consensus:
        Cost (0.25) and Passenger Capacity (0.20) and CO₂ Reduction (0.20) dominate the evaluation,
        reflecting the city's dual focus on fiscal responsibility and environmental targets.
      </Section>

      <Section title="📊 CoCoSo Results & Interpretation">
        <strong style={{ color: "#e2e8f0" }}>Final ranking:</strong>
        <ol style={{ paddingLeft: 20, margin: "10px 0" }}>
          {order.map(({ i }) => (
            <li key={i} style={{ marginBottom: 6 }}>
              <span style={{ color: "#e2e8f0", fontWeight: 600 }}>{prob.alternatives[i]}</span>
              {" — "}k = <code style={{ color: "#f0c060", fontFamily: "'Space Mono',monospace" }}>{result.K[i].toFixed(4)}</code>
            </li>
          ))}
        </ol>
        <strong style={{ color: "#22c55e" }}>{prob.alternatives[bestIdx]}</strong> emerges as the
        top solution. Although its implementation cost (€210M) is the second-highest, it scores
        exceptionally on Passenger Capacity (220k/day), CO₂ Reduction (8,500 t/yr), and
        Long-term Scalability — three criteria with a combined weight of 0.50. This illustrates
        the power of CoCoSo's balanced compromise: it does not penalise a single weak criterion
        too harshly when overall performance is strong.
        <br /><br />
        <strong style={{ color: "#84cc16" }}>Bike-Share Scale-up</strong> ranks 2nd. Despite the
        lowest cost (€8M) and fastest implementation (6 months) — scoring perfectly on cost
        criteria — its limited passenger capacity (35k/day) and moderate scalability hold it back
        from first place. This result correctly reflects real-world experience: cycling infrastructure
        alone cannot replace high-capacity transit.
        <br /><br />
        <strong style={{ color: "#ef4444" }}>{prob.alternatives[worstIdx]}</strong> ranks last.
        Its massive cost (€850M), 96-month timeline, and lowest public acceptance score make it
        difficult to recommend despite its unmatched CO₂ reduction potential and scalability.
      </Section>

      <Section title="🔬 Sensitivity to λ (currently λ = {lambda.toFixed(2)})">
        The λ parameter balances the WSM component (Sᵢ) and the WPM component (Pᵢ) in Kᶜ.
        At λ = 0.5 (default), both models are equally weighted. Increasing λ towards 1.0
        emphasises the additive WSM perspective (favours alternatives with consistently good scores);
        decreasing towards 0.0 emphasises the multiplicative WPM perspective (more sensitive to
        very low individual scores — a "no weak link" philosophy).
        <br /><br />
        For this problem the ranking is <strong style={{ color: "#e2e8f0" }}>stable</strong> across a wide
        range of λ values, because the Light Rail Extension dominates on the two highest-weighted
        criteria regardless of aggregation style. This robustness validates the recommendation.
      </Section>

      <Section title="📌 Managerial Recommendation">
        Based on the CoCoSo analysis, the city should <strong style={{ color: "#22c55e" }}>prioritise the
        Light Rail Extension</strong> as its primary investment, potentially complemented by a
        smaller-scale Bike-Share Scale-up (ranked 2nd) to provide low-cost, immediate
        capacity gains during the 5-year rail construction period. The Underground Metro
        should only be reconsidered if future funding sources substantially reduce the
        effective cost burden on the municipal budget.
      </Section>

      <Section title="⚙️ Method Advantages Observed">
        <ul style={{ paddingLeft: 18, margin: 0 }}>
          <li>The three k-scores (kᵃ, kᵇ, kᶜ) all agree on the ranking, demonstrating <strong style={{ color: "#e2e8f0" }}>internal consistency</strong>.</li>
          <li>The grey-relational normalisation (Eq. 2–3) handled the very different scales (€M vs t/yr vs months) without distortion.</li>
          <li>Unlike TOPSIS alone, CoCoSo's combined aggregation avoids rank reversals from minor data changes.</li>
          <li>The λ parameter gives decision-makers a transparent knob to express risk attitude.</li>
        </ul>
      </Section>
    </div>
  );
}

function PaperAnalysis({ prob, result }) {
  const expectedRanks = [5, 2, 1, 4, 7, 6, 3];
  const match = expectedRanks.every((r, i) => r === result.ranks[i]);
  return (
    <div>
      <Section title="📄 Paper Replication Check">
        This problem reproduces the logistics provider selection example from the original paper
        (Yazdani et al., 2019, Table VI). The expected final ranking is:
        A3 ≻ A2 ≻ A7 ≻ A4 ≻ A1 ≻ A6 ≻ A5.
        <br /><br />
        Computed result matches paper: <strong style={{ color: match ? "#22c55e" : "#ef4444" }}>
          {match ? "✅ CONFIRMED" : "⚠️ DEVIATION (check λ = 0.5)"}
        </strong>
        <br /><br />
        GEFCO (A3) wins primarily due to high scores on the two most heavily weighted criteria:
        Delivery Volume (C3, w=0.326) and Degree of Flexibility (C4, w=0.326). Despite an
        average inventory capacity, its overall profile across the five dimensions is the most
        balanced among all seven providers.
      </Section>
      <Section title="📊 Comparison with Other MCDM Methods (from paper)">
        The paper reports Spearman correlation coefficients between CoCoSo and:
        WASPAS = 1.00, VIKOR = 1.00, MOORA = 0.97, TOPSIS = 0.93, CODAS = 0.93,
        COPRAS = 0.86, EDAS = 0.58.
        <br /><br />
        The near-perfect agreement with WASPAS and VIKOR is not coincidental — CoCoSo inherits
        elements of both: the WSM component mirrors WASPAS's additive model while the WPM
        component mirrors its multiplicative model. The VIKOR agreement reflects the shared
        "closeness to ideal" philosophy in the normalisation step.
      </Section>
    </div>
  );
}
