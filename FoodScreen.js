import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, ScrollView, StyleSheet, ActivityIndicator, Image } from "react-native";
import { addToHistory } from "../utils/storage";
import HistoryPanel from "../components/HistoryPanel";

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



const EXAMPLES = [
  { label: "Nutella", code: "3017620422003" },
  { label: "Coca-Cola", code: "5449000000996" },
  { label: "Evian", code: "3229820129488" },
  { label: "KitKat", code: "7613035153684" },
];

function computeFoodScore(p) {
  const ns = p.nutriscore_grade;
  const map = { a: 92, b: 72, c: 50, d: 28, e: 10 };
  if (map[ns]) return map[ns];
  const n = p.nutriments || {};
  let s = 60;
  if ((n["sugars_100g"] || 0) > 15) s -= 15;
  if ((n["saturated-fat_100g"] || 0) > 5) s -= 15;
  if ((n["salt_100g"] || 0) > 1.5) s -= 10;
  if ((n["fiber_100g"] || 0) > 3) s += 10;
  if ((n["proteins_100g"] || 0) > 8) s += 8;
  return Math.max(5, Math.min(95, s));
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
    <ScrollView style={styles.container} contentContainerStyle={{ paddingBottom: 32 }}>
      <View style={[styles.header, { backgroundColor: ACCENT }]}>
        <Text style={styles.headerTitle}>🥗 Ushqim</Text>
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
          <TouchableOpacity style={styles.searchBtn} onPress={() => search()}>
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

            <VitaminsMineralsCard n={n} />

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
          </>
        )}

        {/* History */}
        <HistoryPanel type="food" accent={ACCENT} />
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f4f7f5" },
  header: { padding: 20, paddingTop: 16 },
  headerTitle: { color: "#fff", fontSize: 24, fontWeight: "800" },
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
}); React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, ScrollView, StyleSheet, ActivityIndicator, Image } from "react-native";

const ACCENT = "#1a7a4a";

const EXAMPLES = [
  { label: "Nutella", code: "3017620422003" },
  { label: "Coca-Cola", code: "5449000000996" },
  { label: "Evian", code: "3229820129488" },
  { label: "KitKat", code: "7613035153684" },
];

function computeFoodScore(p) {
  const ns = p.nutriscore_grade;
  const map = { a: 92, b: 72, c: 50, d: 28, e: 10 };
  if (map[ns]) return map[ns];
  const n = p.nutriments || {};
  let s = 60;
  if ((n["sugars_100g"] || 0) > 15) s -= 15;
  if ((n["saturated-fat_100g"] || 0) > 5) s -= 15;
  if ((n["salt_100g"] || 0) > 1.5) s -= 10;
  if ((n["fiber_100g"] || 0) > 3) s += 10;
  if ((n["proteins_100g"] || 0) > 8) s += 8;
  return Math.max(5, Math.min(95, s));
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
      if (data.status === 1) setProduct(data.product);
      else setError("Produkti nuk u gjet. Provoni një barkod tjetër.");
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
    <ScrollView style={styles.container} contentContainerStyle={{ paddingBottom: 32 }}>
      <View style={[styles.header, { backgroundColor: ACCENT }]}>
        <Text style={styles.headerTitle}>🥗 Ushqim</Text>
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
          <TouchableOpacity style={styles.searchBtn} onPress={() => search()}>
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
          </>
        )}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f4f7f5" },
  header: { padding: 20, paddingTop: 16 },
  headerTitle: { color: "#fff", fontSize: 24, fontWeight: "800" },
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
});
