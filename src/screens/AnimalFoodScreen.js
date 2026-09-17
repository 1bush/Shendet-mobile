import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, ScrollView, StyleSheet, ActivityIndicator, Image } from "react-native";

const ACCENT = "#4a2d8a";

const EXAMPLES = [
  { label: "Purina Pro Plan", code: "3222472852873" },
  { label: "Whiskas", code: "5010394018358" },
  { label: "Royal Canin", code: "3033710080204" },
  { label: "Pedigree", code: "5000239048589" },
];

function isCatFood(p) {
  const cats = (p.categories_tags || []).join(" ").toLowerCase();
  return cats.includes("cat") || cats.includes("felin") || cats.includes("chat");
}

function computeAnimalFoodScore(p) {
  const n = p.nutriments || {};
  let s = 60;
  const protein = n["proteins_100g"] || 0;
  if (protein > 25) s += 20;
  else if (protein > 15) s += 10;
  else if (protein < 5) s -= 20;
  const carbs = n["carbohydrates_100g"] || 0;
  if (carbs > 30) s -= 15;
  else if (carbs < 10) s += 5;
  if ((n["fat_100g"] || 0) > 20) s -= 10;
  const additives = (p.additives_tags || []).length;
  if (additives === 0) s += 10;
  else if (additives > 5) s -= 10;
  return Math.max(5, Math.min(95, s));
}

function getScoreInfo(score) {
  if (score >= 75) return { color: "#7b52d4", label: "Shkëlqyer", emoji: "🌿" };
  if (score >= 50) return { color: "#f1c40f", label: "Mirë", emoji: "😊" };
  if (score >= 25) return { color: "#e67e22", label: "Mesatar", emoji: "😐" };
  return { color: "#e74c3c", label: "Keq", emoji: "⚠️" };
}

export default function AnimalFoodScreen() {
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

  const score = product ? computeAnimalFoodScore(product) : null;
  const info = score !== null ? getScoreInfo(score) : null;
  const n = product?.nutriments || {};
  const isCat = product ? isCatFood(product) : false;

  return (
    <ScrollView style={styles.container} contentContainerStyle={{ paddingBottom: 32 }}>
      <View style={[styles.header, { backgroundColor: ACCENT }]}>
        <Text style={styles.headerTitle}>🐾 Kafshë</Text>
        <Text style={styles.headerSub}>Vlerësoni ushqimin për mace ose qen</Text>
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
              <View style={[styles.typeBadge, { backgroundColor: ACCENT + "22" }]}>
                <Text style={[styles.typeText, { color: ACCENT }]}>{isCat ? "🐱 Për mace" : "🐶 Për qen"}</Text>
              </View>
              <View style={styles.scoreRow}>
                <View style={[styles.scoreBubble, { backgroundColor: info.color }]}>
                  <Text style={styles.scoreNum}>{score}</Text>
                  <Text style={styles.scoreOf}>/100</Text>
                </View>
                <Text style={styles.scoreLabel}>{info.emoji} {info.label}</Text>
              </View>
            </View>

            {n.carbohydrates_100g > 30 && isCat && (
              <View style={[styles.card, { backgroundColor: "#fff5f5", borderLeftWidth: 4, borderLeftColor: "#e74c3c" }]}>
                <Text style={{ color: "#e74c3c", fontWeight: "600" }}>
                  ⚠ Karbohidrate të larta ({Number(n.carbohydrates_100g).toFixed(1)}g/100g) – jo ideale për mace.
                </Text>
              </View>
            )}

            <View style={styles.card}>
              <Text style={styles.sectionTitle}>Vlerat ushqyese (për 100g)</Text>
              {[
                { label: "Energji", val: n["energy-kcal_100g"], unit: "kcal" },
                { label: "Proteina", val: n.proteins_100g, unit: "g" },
                { label: "Yndyrë", val: n.fat_100g, unit: "g", warn: n.fat_100g > 20 },
                { label: "Karbohidrate", val: n.carbohydrates_100g, unit: "g", warn: n.carbohydrates_100g > 30 },
                { label: "Fibra", val: n.fiber_100g, unit: "g" },
                { label: "Kripë", val: n.salt_100g, unit: "g" },
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

            {(product.additives_tags || []).length > 0 && (
              <View style={styles.card}>
                <Text style={styles.sectionTitle}>Aditivë ({product.additives_tags.length})</Text>
                <View style={{ flexDirection: "row", flexWrap: "wrap", gap: 6 }}>
                  {product.additives_tags.map(a => (
                    <View key={a} style={styles.additiveBadge}>
                      <Text style={styles.additiveText}>{a.replace("en:", "").toUpperCase()}</Text>
                    </View>
                  ))}
                </View>
              </View>
            )}
          </>
        )}
      </View>
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#f6f4fb" },
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
  typeBadge: { alignSelf: "center", paddingHorizontal: 12, paddingVertical: 6, borderRadius: 20, marginTop: 10 },
  typeText: { fontWeight: "700", fontSize: 14 },
  scoreRow: { flexDirection: "row", alignItems: "center", gap: 16, marginTop: 16 },
  scoreBubble: { width: 70, height: 70, borderRadius: 35, alignItems: "center", justifyContent: "center" },
  scoreNum: { color: "#fff", fontSize: 22, fontWeight: "800" },
  scoreOf: { color: "rgba(255,255,255,0.8)", fontSize: 10 },
  scoreLabel: { fontSize: 20 },
  sectionTitle: { fontSize: 12, fontWeight: "700", textTransform: "uppercase", color: "#888", letterSpacing: 1, marginBottom: 10 },
  nutriRow: { flexDirection: "row", justifyContent: "space-between", paddingVertical: 6, borderBottomWidth: 1, borderBottomColor: "#f0f0f0" },
  nutriLabel: { color: "#666", fontSize: 14 },
  nutriVal: { fontWeight: "600", fontSize: 14 },
  ingredients: { fontSize: 13, color: "#555", lineHeight: 20 },
  errorCard: { backgroundColor: "#fff0f0", borderRadius: 12, padding: 16, borderLeftWidth: 4, borderLeftColor: "#e74c3c" },
  errorText: { color: "#e74c3c", fontWeight: "600" },
  additiveBadge: { backgroundColor: "#f0f0f0", paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  additiveText: { fontSize: 11, color: "#555", fontFamily: "monospace" },
});
