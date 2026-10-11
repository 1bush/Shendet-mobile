import React, { useState, useEffect } from "react";
import { View, Text, TextInput, TouchableOpacity, ScrollView, StyleSheet, ActivityIndicator, Image } from "react-native";
import { addToHistory } from "../utils/storage";
import HistoryPanel from "../components/HistoryPanel";
import BarcodeScanner from "../components/BarcodeScanner";
import BounceIcon from "../components/BounceIcon";
import {
  getAdditiveInfo,
  additivePenalty,
  hasOrganicLabel,
  getNovaInfo,
  getLabels,
  fetchAlternatives,
  RISK,
} from "../lib/productInfo";

const ACCENT = "#1a7a4a";

// ─── Vitamina & Minerale ──────────────────────────────────────────────────────

const VITAMINS = [
  { key: "vitamin-a_100g",   label: "Vitamina A",   unit: "µg",  rda: 900,   emoji: "🟠", benefit: "Shikimi & imuniteti" },
  { key: "vitamin-c_100g",   label: "Vitamina C",   unit: "mg",  rda: 90,    emoji: "🟡", benefit: "Antioksidant & lëkura" },
  { key: "vitamin-d_100g",   label: "Vitamina D",   unit: "µg",  rda: 20,    emoji: "☀️",  benefit: "Kockat & imuniteti" },
  { key: "vitamin-e_100g",   label: "Vitamina E",   unit: "mg",  rda: 15,    emoji: "🟢", benefit: "Antioksidant" },
  { key: "vitamin-k_100g",   label: "Vitamina K",   unit: "µg",  rda: 120,   emoji: "🟤", benefit: "Koagulimi i gjakut" },
  { key: "vitamin-b1_100g",  label: "B1 Thiamine",  unit: "mg",  rda: 1.2,   emoji: "🔵", benefit: "Energjia & nervat" },
  { key: "vitamin-b2_100g",  label: "B2 Riboflavin",unit: "mg",  rda: 1.3,   emoji: "🔵", benefit: "Metabolizmi" },
  { key: "vitamin-b3_100g",  label: "B3 Niacin",    unit: "mg",  rda: 16,    emoji: "🔵", benefit: "Lëkura & nervat" },
  { key: "vitamin-b6_100g",  label: "B6",           unit: "mg",  rda: 1.7,   emoji: "🔵", benefit: "Proteina & trurit" },
  { key: "vitamin-b9_100g",  label: "B9 Acid folik",unit: "µg",  rda: 400,   emoji: "🔵", benefit: "ADN & shtatzënia" },
  { key: "vitamin-b12_100g", label: "B12",          unit: "µg",  rda: 2.4,   emoji: "🔴", benefit: "Gjaku & nervat" },
];

const MINERALS = [
  { key: "calcium_100g",    label: "Kalcium",   unit: "mg",  rda: 1000, emoji: "🦴", benefit: "Kockat & dhëmbët" },
  { key: "iron_100g",       label: "Hekur",     unit: "mg",  rda: 18,   emoji: "🩸", benefit: "Hemoglobina" },
  { key: "magnesium_100g",  label: "Magnez",    unit: "mg",  rda: 420,  emoji: "💪", benefit: "Muskujt & nervat" },
  { key: "phosphorus_100g", label: "Fosfor",    unit: "mg",  rda: 700,  emoji: "⚡", benefit: "Kockat & energjia" },
  { key: "potassium_100g",  label: "Kalium",    unit: "mg",  rda: 4700, emoji: "🫀", benefit: "Zemra & presioni" },
  { key: "zinc_100g",       label: "Zink",      unit: "mg",  rda: 11,   emoji: "🛡️",  benefit: "Imuniteti & lëkura" },
  { key: "selenium_100g",   label: "Selen",     unit: "µg",  rda: 55,   emoji: "🔬", benefit: "Antioksidant" },
  { key: "copper_100g",     label: "Bakër",     unit: "mg",  rda: 0.9,  emoji: "🟤", benefit: "Gjaku & kockat" },
  { key: "manganese_100g",  label: "Mangan",    unit: "mg",  rda: 2.3,  emoji: "🟫", benefit: "Metabolizmi" },
  { key: "iodine_100g",     label: "Jod",       unit: "µg",  rda: 150,  emoji: "🌊", benefit: "Tiroida" },
  { key: "sodium_100g",     label: "Natrium",   unit: "mg",  rda: 2300, emoji: "🧂", benefit: "Ekuilibri i lëngjeve", warn: true },
];

function pct(val, rda) {
  return Math.min(100, Math.round((val / rda) * 100));
}

function barColor(p, warn) {
  if (warn) return p > 80 ? "#e74c3c" : "#e67e22";
  if (p >= 50) return "#27ae60";
  if (p >= 20) return "#f1c40f";
  return "#60a5fa";
}

function VitaminsMineralsCard({ n }) {
  const [expanded, setExpanded] = useState(false);

  const foundVits = VITAMINS.filter(v => n[v.key] != null && !isNaN(n[v.key]) && n[v.key] > 0);
  const foundMins = MINERALS.filter(m => n[m.key] != null && !isNaN(n[m.key]) && n[m.key] > 0);

  if (foundVits.length === 0 && foundMins.length === 0) return null;

  const renderRow = (item, isWarn) => {
    const val = n[item.key];
    const p = pct(val, item.rda);
    const color = barColor(p, isWarn);
    return (
      <View key={item.key} style={vmStyles.row}>
        <Text style={vmStyles.emoji}>{item.emoji}</Text>
        <View style={vmStyles.info}>
          <View style={vmStyles.labelRow}>
            <Text style={vmStyles.label}>{item.label}</Text>
            <Text style={vmStyles.benefit}>{item.benefit}</Text>
          </View>
          <View style={vmStyles.barTrack}>
            <View style={[vmStyles.barFill, { width: `${p}%`, backgroundColor: color }]} />
          </View>
          <View style={vmStyles.valRow}>
            <Text style={[vmStyles.pctText, { color }]}>{p}% RDA</Text>
            <Text style={vmStyles.valText}>{Number(val).toFixed(val < 1 ? 2 : 1)} {item.unit}</Text>
          </View>
        </View>
      </View>
    );
  };

  return (
    <View style={[styles.card, { padding: 0, overflow: "hidden" }]}>
      <TouchableOpacity
        style={vmStyles.header}
        onPress={() => setExpanded(e => !e)}
        activeOpacity={0.8}
      >
        <View>
          <Text style={vmStyles.title}>💊 Vitamina & Minerale</Text>
          <Text style={vmStyles.subtitle}>
            {foundVits.length} vitamina · {foundMins.length} minerale të gjetura
          </Text>
        </View>
        <Text style={vmStyles.chevron}>{expanded ? "▲" : "▼"}</Text>
      </TouchableOpacity>

      {expanded && (
        <View style={{ padding: 16 }}>
          {foundVits.length > 0 && (
            <>
              <Text style={vmStyles.groupTitle}>VITAMINA</Text>
              {foundVits.map(v => renderRow(v, false))}
            </>
          )}
          {foundMins.length > 0 && (
            <>
              <Text style={[vmStyles.groupTitle, { marginTop: foundVits.length > 0 ? 14 : 0 }]}>MINERALE</Text>
              {foundMins.map(m => renderRow(m, m.warn))}
            </>
          )}
          <Text style={vmStyles.rdaNote}>* RDA = Doza ditore e rekomanduar për të rritur (për 100g produkt)</Text>
        </View>
      )}
    </View>
  );
}

const vmStyles = StyleSheet.create({
  header: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", padding: 16, backgroundColor: "#f8fffe", borderRadius: 16 },
  title: { fontSize: 14, fontWeight: "800", color: "#1a7a4a" },
  subtitle: { fontSize: 11, color: "#888", marginTop: 2 },
  chevron: { fontSize: 12, color: "#888" },
  groupTitle: { fontSize: 10, fontWeight: "800", letterSpacing: 2, color: "#aaa", marginBottom: 10, textTransform: "uppercase" },
  row: { flexDirection: "row", alignItems: "flex-start", gap: 10, marginBottom: 12 },
  emoji: { fontSize: 18, width: 24, textAlign: "center", marginTop: 2 },
  info: { flex: 1 },
  labelRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "baseline", marginBottom: 4 },
  label: { fontSize: 13, fontWeight: "700", color: "#333" },
  benefit: { fontSize: 10, color: "#aaa" },
  barTrack: { height: 6, backgroundColor: "#f0f0f0", borderRadius: 3, overflow: "hidden" },
  barFill: { height: "100%", borderRadius: 3 },
  valRow: { flexDirection: "row", justifyContent: "space-between", marginTop: 3 },
  pctText: { fontSize: 11, fontWeight: "700" },
  valText: { fontSize: 11, color: "#999" },
  rdaNote: { fontSize: 10, color: "#bbb", marginTop: 8, textAlign: "center", fontStyle: "italic" },
});



// ─── Kartat e reja (stili Yuka) ──────────────────────────────────────────────

/** Aditivët me nivele rreziku — si kartë analize te Yuka. */
function AdditivesCard({ p }) {
  const tags = p.additives_tags || [];
  return (
    <View style={styles.card}>
      <Text style={styles.sectionTitle}>Aditivë ({tags.length})</Text>
      {tags.length === 0 ? (
        <View style={adStyles.okRow}>
          <Text style={adStyles.okText}>✅ Nuk u gjetën aditivë të shtuar</Text>
        </View>
      ) : (
        tags.map((t) => {
          const info = getAdditiveInfo(t);
          const risk = RISK[info.risk] || RISK.unknown;
          return (
            <View key={t} style={adStyles.row}>
              <View style={[adStyles.codeBadge, { backgroundColor: risk.color }]}>
                <Text style={adStyles.codeText}>{info.code}</Text>
              </View>
              <View style={{ flex: 1 }}>
                <View style={adStyles.nameRow}>
                  <Text style={adStyles.name}>{info.name || info.code}</Text>
                  <View style={[adStyles.riskChip, { backgroundColor: risk.color + "22", borderColor: risk.color }]}>
                    <Text style={[adStyles.riskText, { color: risk.color }]}>{risk.label}</Text>
                  </View>
                </View>
                <Text style={adStyles.desc}>{info.desc}</Text>
              </View>
            </View>
          );
        })
      )}
    </View>
  );
}

const adStyles = StyleSheet.create({
  okRow: { backgroundColor: "#f0faf4", borderRadius: 10, padding: 12 },
  okText: { color: "#27ae60", fontWeight: "700", fontSize: 13 },
  row: { flexDirection: "row", gap: 10, marginBottom: 12, alignItems: "flex-start" },
  codeBadge: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6, minWidth: 52, alignItems: "center" },
  codeText: { color: "#fff", fontWeight: "800", fontSize: 11 },
  nameRow: { flexDirection: "row", justifyContent: "space-between", alignItems: "center", marginBottom: 2 },
  name: { fontSize: 13, fontWeight: "700", color: "#333", flex: 1 },
  riskChip: { paddingHorizontal: 6, paddingVertical: 2, borderRadius: 6, borderWidth: 1, marginLeft: 6 },
  riskText: { fontSize: 9, fontWeight: "800" },
  desc: { fontSize: 12, color: "#777", lineHeight: 17 },
});

/** Grupi NOVA: 1–4 me hapëzim vizual. */
function NovaCard({ p }) {
  const g = Number(p.nova_group);
  const info = getNovaInfo(g);
  if (!info) return null;
  return (
    <View style={styles.card}>
      <Text style={styles.sectionTitle}>Grupi NOVA — shkalla e përpunimit</Text>
      <View style={novaStyles.steps}>
        {[1, 2, 3, 4].map((i) => (
          <View
            key={i}
            style={[novaStyles.step, { backgroundColor: i === g ? getNovaInfo(i).color : "#eee" }]}
          >
            <Text style={[novaStyles.stepNum, i === g && { color: "#fff" }]}>{i}</Text>
          </View>
        ))}
      </View>
      <Text style={[novaStyles.title, { color: info.color }]}>{info.title}</Text>
      <Text style={novaStyles.desc}>{info.desc}</Text>
    </View>
  );
}

const novaStyles = StyleSheet.create({
  steps: { flexDirection: "row", gap: 6, marginBottom: 10 },
  step: { flex: 1, height: 26, borderRadius: 6, alignItems: "center", justifyContent: "center" },
  stepNum: { fontWeight: "800", fontSize: 12, color: "#999" },
  title: { fontSize: 14, fontWeight: "800", marginBottom: 4 },
  desc: { fontSize: 12, color: "#666", lineHeight: 18 },
});

/** Etiketat e njohura (BIO, vegan, pa gluten...) si chip-a. */
function LabelsCard({ p }) {
  const labels = getLabels(p);
  if (labels.length === 0) return null;
  return (
    <View style={styles.card}>
      <Text style={styles.sectionTitle}>Etiketat</Text>
      <View style={{ flexDirection: "row", flexWrap: "wrap", gap: 8 }}>
        {labels.map((l) => (
          <View key={l.text} style={[labStyles.chip, { backgroundColor: l.color + "1a", borderColor: l.color }]}>
            <Text style={[labStyles.text, { color: l.color }]}>{l.text}</Text>
          </View>
        ))}
      </View>
    </View>
  );
}

const labStyles = StyleSheet.create({
  chip: { paddingHorizontal: 10, paddingVertical: 6, borderRadius: 20, borderWidth: 1 },
  text: { fontSize: 12, fontWeight: "700" },
});

/**
 * Alternativa më të shëndetshme nga e njëjta kategori (tipari kryesor i Yuka-s).
 * Shfaqet kur produkti nuk është grade A / pikë < 75; klikimi e kërkon atë.
 */
function AlternativesCard({ p, score, onSelect }) {
  const [state, setState] = useState({ loading: true, items: [] });
  const code = p.code;

  useEffect(() => {
    let alive = true;
    setState({ loading: true, items: [] });
    fetchAlternatives(p)
      .then((items) => { if (alive) setState({ loading: false, items }); })
      .catch(() => { if (alive) setState({ loading: false, items: [] }); });
    return () => { alive = false; };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [code]);

  if (score >= 75) return null;

  if (state.loading) {
    return (
      <View style={styles.card}>
        <Text style={styles.sectionTitle}>🔄 Alternativa më të shëndetshme</Text>
        <View style={altStyles.loadingRow}>
          <ActivityIndicator color={ACCENT} size="small" />
          <Text style={altStyles.loadingText}>Duke kërkuar alternativa...</Text>
        </View>
      </View>
    );
  }

  if (state.items.length === 0) return null;

  const NS_COLORS = { a: "#038141", b: "#85bb2f", c: "#fecb02", d: "#ee8100", e: "#e63e11" };

  return (
    <View style={styles.card}>
      <Text style={styles.sectionTitle}>🔄 Alternativa më të shëndetshme</Text>
      <Text style={altStyles.hint}>Produkte të së njëjtit lloj me NutriScore më të mirë:</Text>
      {state.items.map((it) => {
        const g = (it.nutriscore_grade || "").toLowerCase();
        return (
          <TouchableOpacity
            key={it.code}
            style={altStyles.row}
            activeOpacity={0.7}
            onPress={() => onSelect(it.code)}
          >
            {it.image_front_small_url ? (
              <Image source={{ uri: it.image_front_small_url }} style={altStyles.img} />
            ) : (
              <View style={[altStyles.img, altStyles.imgFallback]}>
                <Text style={{ fontSize: 18 }}>📦</Text>
              </View>
            )}
            <View style={{ flex: 1 }}>
              <Text style={altStyles.name} numberOfLines={1}>{it.product_name}</Text>
              {!!it.brands && <Text style={altStyles.brand} numberOfLines={1}>{it.brands}</Text>}
            </View>
            <View style={[altStyles.grade, { backgroundColor: NS_COLORS[g] || "#ccc" }]}>
              <Text style={altStyles.gradeText}>{(g || "?").toUpperCase()}</Text>
            </View>
          </TouchableOpacity>
        );
      })}
      <Text style={altStyles.footNote}>Prek një alternativë për ta parë.</Text>
    </View>
  );
}

const altStyles = StyleSheet.create({
  loadingRow: { flexDirection: "row", alignItems: "center", gap: 10, paddingVertical: 6 },
  loadingText: { fontSize: 12, color: "#888" },
  hint: { fontSize: 12, color: "#888", marginBottom: 10 },
  row: { flexDirection: "row", alignItems: "center", gap: 10, paddingVertical: 8, borderBottomWidth: 1, borderBottomColor: "#f2f2f2" },
  img: { width: 44, height: 44, borderRadius: 8, backgroundColor: "#f4f4f4" },
  imgFallback: { alignItems: "center", justifyContent: "center" },
  name: { fontSize: 13, fontWeight: "700", color: "#333" },
  brand: { fontSize: 11, color: "#999", marginTop: 2 },
  grade: { width: 30, height: 30, borderRadius: 15, alignItems: "center", justifyContent: "center" },
  gradeText: { color: "#fff", fontWeight: "800", fontSize: 13 },
  footNote: { fontSize: 10, color: "#bbb", marginTop: 8, textAlign: "center" },
});

/**
 * Sheqeri në kuba + kripa në majë gishtash (si Yuka).
 * Pragjet sipas semaforit britanik (FSA) për 100g:
 *  sheqer: ≤5 pak (jeshil), ≤22.5 mesatar (portokalli), >22.5 shumë (kuq)
 *  kripë:  ≤0.3 pak, ≤1.5 mesatar, >1.5 shumë
 * 1 kub sheqeri ≈ 4g · 1 majë gishtash kripë ≈ 0.5g
 */
function SugarSaltCard({ n }) {
  const sugars = Number(n.sugars_100g);
  const salt = Number(n.salt_100g);
  const hasSugar = !isNaN(sugars) && sugars != null;
  const hasSalt = !isNaN(salt) && salt != null;
  if (!hasSugar && !hasSalt) return null;

  const level = (v, lo, hi) =>
    v > hi ? { c: "#e74c3c", t: "Shumë" } : v > lo ? { c: "#e67e22", t: "Mesatar" } : { c: "#27ae60", t: "Pak" };

  const cubes = (g) => {
    const full = Math.floor(g / 4);
    const half = g % 4 >= 2;
    const shown = Math.min(full, 12);
    let s = "🟫".repeat(shown);
    if (half && shown < 12) s += "◐";
    if (full > 12) s += `  +${full - 12} të tjera`;
    return { txt: s || "—", n: full + (half ? 0.5 : 0) };
  };

  const pinches = (g) => {
    const full = Math.floor(g / 0.5);
    const shown = Math.min(full, 12);
    let s = "🤏".repeat(shown);
    if (full > 12) s += `  +${full - 12} të tjera`;
    return { txt: s || "—", n: full };
  };

  const rows = [];
  if (hasSugar) {
    const lv = level(sugars, 5, 22.5);
    const cb = cubes(sugars);
    rows.push(
      <View key="sugar" style={ssStyles.row}>
        <View style={[ssStyles.dot, { backgroundColor: lv.c }]} />
        <View style={{ flex: 1 }}>
          <View style={ssStyles.line}>
            <Text style={ssStyles.label}>🧊 Sheqeri</Text>
            <Text style={[ssStyles.val, { color: lv.c }]}>{sugars.toFixed(1)}g · {cb.n} kube · {lv.t}</Text>
          </View>
          <Text style={ssStyles.icons}>{cb.txt}</Text>
        </View>
      </View>
    );
  }
  if (hasSalt) {
    const lv = level(salt, 0.3, 1.5);
    const pc = pinches(salt);
    rows.push(
      <View key="salt" style={ssStyles.row}>
        <View style={[ssStyles.dot, { backgroundColor: lv.c }]} />
        <View style={{ flex: 1 }}>
          <View style={ssStyles.line}>
            <Text style={ssStyles.label}>🧂 Kripa</Text>
            <Text style={[ssStyles.val, { color: lv.c }]}>{salt.toFixed(1)}g · {pc.n} majë gishtash · {lv.t}</Text>
          </View>
          <Text style={ssStyles.icons}>{pc.txt}</Text>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.card}>
      <Text style={styles.sectionTitle}>Sheqeri & kripa vizualisht (për 100g)</Text>
      {rows}
      <Text style={ssStyles.note}>1 kub sheqeri ≈ 4g · 1 majë gishtash ≈ 0.5g kripë</Text>
    </View>
  );
}

const ssStyles = StyleSheet.create({
  row: { flexDirection: "row", gap: 10, marginBottom: 12, alignItems: "flex-start" },
  dot: { width: 12, height: 12, borderRadius: 6, marginTop: 4 },
  line: { flexDirection: "row", justifyContent: "space-between", alignItems: "baseline", marginBottom: 4 },
  label: { fontSize: 14, fontWeight: "700", color: "#333" },
  val: { fontSize: 12, fontWeight: "700" },
  icons: { fontSize: 16, lineHeight: 24 },
  note: { fontSize: 10, color: "#bbb", textAlign: "center", marginTop: 4 },
});

const EXAMPLES = [
  { label: "Nutella", code: "3017620422003" },
  { label: "Coca-Cola", code: "5449000000996" },
  { label: "Evian", code: "3229820129488" },
  { label: "KitKat", code: "7613035153684" },
];

function computeFoodScore(p) {
  const ns = p.nutriscore_grade;
  const map = { a: 92, b: 72, c: 50, d: 28, e: 10 };
  let s;
  if (map[ns]) {
    s = map[ns];
  } else {
    const n = p.nutriments || {};
    s = 60;
    if ((n["sugars_100g"] || 0) > 15) s -= 15;
    if ((n["saturated-fat_100g"] || 0) > 5) s -= 15;
    if ((n["salt_100g"] || 0) > 1.5) s -= 10;
    if ((n["fiber_100g"] || 0) > 3) s += 10;
    if ((n["proteins_100g"] || 0) > 8) s += 8;
    s = Math.max(5, Math.min(95, s));
  }
  // Si Yuka: aditivët e rrezikshëm ulin pikët (maks. -21), etiketa BIO i shton (+5)
  s -= additivePenalty(p.additives_tags);
  if (hasOrganicLabel(p)) s += 5;
  return Math.max(3, Math.min(98, s));
}

function getScoreInfo(score) {
  if (score >= 75) return { color: "#27ae60", label: "Shkëlqyer", emoji: "🌿" };
  if (score >= 50) return { color: "#f1c40f", label: "Mirë", emoji: "😊" };
  if (score >= 25) return { color: "#e67e22", label: "Mesatar", emoji: "😐" };
  return { color: "#e74c3c", label: "Keq", emoji: "⚠️" };
}

export default function FoodScreen() {
  const [code, setCode] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [product, setProduct] = useState(null);
  const [skaner, setSkaner] = useState(false);

  async function search(val) {
    const c = (val || code).trim();
    if (!c) return;
    setCode(c);
    setLoading(true);
    setError(null);
    setProduct(null);
    try {
      const res = await fetch(`https://world.openfoodfacts.org/api/v0/product/${c}.json`);
      const data = await res.json();
      if (data.status === 1) {
        const p = data.product;
        setProduct(p);
        const score = computeFoodScore(p);
        const n = p.nutriments || {};
        await addToHistory("food", {
          name: p.product_name || "Pa emër",
          score,
          barcode: c,
          nutrients: {
            energy: n["energy-kcal_100g"],
            sugars: n.sugars_100g,
            fat: n.fat_100g,
            proteins: n.proteins_100g,
            salt: n.salt_100g,
          },
        });
      } else {
        setError("Produkti nuk u gjet. Provoni një barkod tjetër.");
      }
    } catch {
      setError("Gabim në lidhjen me serverin.");
    } finally {
      setLoading(false);
    }
  }

  const score = product ? computeFoodScore(product) : null;
  const info = score !== null ? getScoreInfo(score) : null;
  const n = product?.nutriments || {};

  return (
    <>
      <ScrollView style={styles.container} contentContainerStyle={{ paddingBottom: 32 }}>

      <View style={styles.logoRow}>
        <Image source={require("../../assets/logo.png")} style={styles.logo} />
        <Text style={styles.logoText}>SHËNDETI-IM</Text>
      </View>
      <View style={[styles.header, { backgroundColor: ACCENT }]}>
        <View style={[styles.headerBadge, { backgroundColor: ACCENT + "22" }]}>
  <BounceIcon source={require("../../assets/food.png")} size={34} style={styles.headerBadgeImg} />
  <Text style={styles.headerBadgeText}>Ushqim</Text>
</View>
        <Text style={styles.headerSub}>Skanoni barkodin për të zbuluar përbërjen</Text>
        <View style={styles.inputRow}>
          <TextInput
            style={styles.input}
            value={code}
            onChangeText={setCode}
            placeholder="Vendos barkodin..."
            keyboardType="numeric"
            onSubmitEditing={() => search()}
          />
          <TouchableOpacity style={styles.searchBtn} onPress={() => setSkaner(true)}>
            <Text style={{ color: "#fff", fontSize: 18 }}>📷</Text>
          </TouchableOpacity>
        </View>
        <View style={styles.exampleRow}>
          {EXAMPLES.map(ex => (
            <TouchableOpacity key={ex.code} style={styles.exampleBtn} onPress={() => search(ex.code)}>
              <Text style={styles.exampleText}>{ex.label}</Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      <View style={styles.body}>
        {loading && <ActivityIndicator color={ACCENT} size="large" style={{ marginTop: 32 }} />}
        {error && <View style={styles.errorCard}><Text style={styles.errorText}>⚠ {error}</Text></View>}
        {product && (
          <>
            <View style={styles.card}>
              {product.image_front_small_url && (
                <Image source={{ uri: product.image_front_small_url }} style={styles.productImage} />
              )}
              <Text style={styles.productName}>{product.product_name || "Pa emër"}</Text>
              {product.brands && <Text style={styles.brandText}>{product.brands}</Text>}
              <View style={styles.scoreRow}>
                <View style={[styles.scoreBubble, { backgroundColor: info.color }]}>
                  <Text style={styles.scoreNum}>{score}</Text>
                  <Text style={styles.scoreOf}>/100</Text>
                </View>
                <Text style={styles.scoreLabel}>{info.emoji} {info.label}</Text>
              </View>
            </View>

            {product.nutriscore_grade && (
              <View style={styles.card}>
                <Text style={styles.sectionTitle}>NutriScore</Text>
                <View style={{ flexDirection: "row", gap: 6 }}>
                  {["a","b","c","d","e"].map(g => {
                    const colors = { a:"#038141",b:"#85bb2f",c:"#fecb02",d:"#ee8100",e:"#e63e11" };
                    return (
                      <View key={g} style={[styles.nsBox, { backgroundColor: colors[g], opacity: product.nutriscore_grade === g ? 1 : 0.3 }]}>
                        <Text style={styles.nsText}>{g.toUpperCase()}</Text>
                      </View>
                    );
                  })}
                </View>
              </View>
            )}

            <LabelsCard p={product} />

            <View style={styles.card}>
              <Text style={styles.sectionTitle}>Vlerat ushqyese (për 100g)</Text>
              {[
                { label: "Energji", val: n["energy-kcal_100g"], unit: "kcal" },
                { label: "Yndyrë", val: n.fat_100g, unit: "g" },
                { label: "Yndyrë e ngopur", val: n["saturated-fat_100g"], unit: "g", warn: n["saturated-fat_100g"] > 5 },
                { label: "Karbohidrate", val: n.carbohydrates_100g, unit: "g" },
                { label: "Sheqer", val: n.sugars_100g, unit: "g", warn: n.sugars_100g > 15 },
                { label: "Fibra", val: n.fiber_100g, unit: "g" },
                { label: "Proteina", val: n.proteins_100g, unit: "g" },
                { label: "Kripë", val: n.salt_100g, unit: "g", warn: n.salt_100g > 1.5 },
              ].map(row => row.val != null && !isNaN(row.val) ? (
                <View key={row.label} style={styles.nutriRow}>
                  <Text style={styles.nutriLabel}>{row.label}</Text>
                  <Text style={[styles.nutriVal, row.warn && { color: "#e74c3c" }]}>
                    {Number(row.val).toFixed(row.unit === "kcal" ? 0 : 1)} {row.unit}
                  </Text>
                </View>
              ) : null)}
            </View>

            <SugarSaltCard n={n} />

            <VitaminsMineralsCard n={n} />

            <NovaCard p={product} />

            <AdditivesCard p={product} />

            {product.ingredients_text && (
              <View style={styles.card}>
                <Text style={styles.sectionTitle}>Përbërësit</Text>
                <Text style={styles.ingredients}>{product.ingredients_text}</Text>
              </View>
            )}

            {product.allergens && product.allergens.length > 0 && (
              <View style={[styles.card, styles.allergenCard]}>
                <Text style={styles.allergenTitle}>⚠ Alergjenët</Text>
                <Text style={styles.allergenText}>{product.allergens.replace(/en:/g, "")}</Text>
              </View>
            )}

            <AlternativesCard p={product} score={score} onSelect={search} />
          </>
        )}

        {/* History */}
        <HistoryPanel type="food" accent={ACCENT} />
      </View>
    </ScrollView>
      {/* Skaneri i barkodit — hapet me butonin 📷 */}
      {skaner && (
        <View style={styles.scannerOverlay}>
          <BarcodeScanner
            onScan={(data) => {
              setSkaner(false);
              search(data);
            }}
            onClose={() => setSkaner(false)}
            accent={ACCENT}
          />
        </View>
      )}
    </>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f4f7f5" },
  header: { padding: 20, paddingTop: 16 },
  headerTitle: { color: "#fff", fontSize: 24, fontWeight: "800" },
  headerBadge: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    borderRadius: 14,
  },
  headerBadgeImg: {
    width: 34,
    height: 34,
    borderRadius: 9,
  },
  headerBadgeText: {
    color: "#fff",
    fontWeight: "800",
    fontSize: 18,
  },
  headerSub: { color: "rgba(255,255,255,0.85)", fontSize: 13, marginTop: 4 },
  inputRow: { flexDirection: "row", gap: 10, marginTop: 16 },
  input: { flex: 1, backgroundColor: "#fff", borderRadius: 14, paddingHorizontal: 16, paddingVertical: 12, fontSize: 16 },
  searchBtn: { width: 50, backgroundColor: "rgba(255,255,255,0.2)", borderRadius: 14, alignItems: "center", justifyContent: "center", borderWidth: 1, borderColor: "rgba(255,255,255,0.3)" },
  exampleRow: { flexDirection: "row", flexWrap: "wrap", gap: 8, marginTop: 12 },
  exampleBtn: { backgroundColor: "rgba(255,255,255,0.15)", paddingHorizontal: 12, paddingVertical: 6, borderRadius: 20, borderWidth: 1, borderColor: "rgba(255,255,255,0.25)" },
  exampleText: { color: "#fff", fontSize: 12 },
  body: { padding: 16, gap: 12 },
  card: { backgroundColor: "#fff", borderRadius: 16, padding: 16, shadowColor: "#000", shadowOpacity: 0.06, shadowRadius: 8, elevation: 2 },
  productImage: { width: 80, height: 80, borderRadius: 10, alignSelf: "center", marginBottom: 12 },
  productName: { fontSize: 18, fontWeight: "700", textAlign: "center" },
  brandText: { color: "#888", fontSize: 13, textAlign: "center", marginTop: 4 },
  scoreRow: { flexDirection: "row", alignItems: "center", gap: 16, marginTop: 16 },
  scoreBubble: { width: 70, height: 70, borderRadius: 35, alignItems: "center", justifyContent: "center" },
  scoreNum: { color: "#fff", fontSize: 22, fontWeight: "800" },
  scoreOf: { color: "rgba(255,255,255,0.8)", fontSize: 10 },
  scoreLabel: { fontSize: 20 },
  sectionTitle: { fontSize: 12, fontWeight: "700", textTransform: "uppercase", color: "#888", letterSpacing: 1, marginBottom: 10 },
  nsBox: { flex: 1, alignItems: "center", paddingVertical: 8, borderRadius: 8 },
  nsText: { color: "#fff", fontWeight: "800", fontSize: 16 },
  nutriRow: { flexDirection: "row", justifyContent: "space-between", paddingVertical: 6, borderBottomWidth: 1, borderBottomColor: "#f0f0f0" },
  nutriLabel: { color: "#666", fontSize: 14 },
  nutriVal: { fontWeight: "600", fontSize: 14 },
  ingredients: { fontSize: 13, color: "#555", lineHeight: 20 },
  errorCard: { backgroundColor: "#fff0f0", borderRadius: 12, padding: 16, borderLeftWidth: 4, borderLeftColor: "#e74c3c" },
  errorText: { color: "#e74c3c", fontWeight: "600" },
  allergenCard: { backgroundColor: "#fff5f5", borderLeftWidth: 4, borderLeftColor: "#e74c3c" },
  allergenTitle: { color: "#e74c3c", fontWeight: "700", marginBottom: 6 },
  allergenText: { color: "#555", fontSize: 13 },
  scannerOverlay: {
    position: "absolute", top: 0, left: 0, right: 0, bottom: 0,
    backgroundColor: "#000", zIndex: 100,
  },
  logoRow: {
    alignItems: "center",
    paddingTop: 12,
    paddingBottom: 8,
  },
  logo: {
    width: 52,
    height: 52,
    borderRadius: 14,
    backgroundColor: "#1a7a4a",
  },
  logoText: {
    color: "#1a7a4a",
    fontWeight: "800",
    fontSize: 18,
    letterSpacing: 1.5,
    marginTop: 6,
  },
});
