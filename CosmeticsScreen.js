import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, ScrollView, StyleSheet, ActivityIndicator, Image } from "react-native";

const ACCENT = "#c0427a";

const EXAMPLES = [
  { label: "L'Oréal Elvive", code: "3600550345407" },
  { label: "Garnier Micellar", code: "3574661385181" },
  { label: "Nivea Soft", code: "3600523599936" },
  { label: "Dove", code: "8710447394519" },
];

const DANGEROUS = [
  { key: "paraben", label: "Paraben", risk: "Ndërhyn me hormonet" },
  { key: "formaldehyde", label: "Formaldehyde", risk: "Kancerogjenik" },
  { key: "triclosan", label: "Triclosan", risk: "Ndërhyn me funksionin tiroidal" },
  { key: "phthalate", label: "Phthalate", risk: "Ndërhyn me sistemin hormonal" },
  { key: "sodium lauryl sulfate", label: "SLS", risk: "Irritues i lëkurës" },
];

function computeCosmeticScore(p) {
  let s = 70;
  const ings = (p.ingredients_text || "").toLowerCase();
  DANGEROUS.forEach(d => { if (ings.includes(d.key)) s -= 15; });
  ["aloe vera","jojoba","argan","shea butter","vitamin e","coconut"].forEach(n => { if (ings.includes(n)) s += 5; });
  const labels = (p.labels_tags || []).join(" ");
  if (labels.includes("organic") || labels.includes("bio")) s += 10;
  if (labels.includes("natural")) s += 5;
  if (labels.includes("cruelty-free") || labels.includes("vegan")) s += 8;
  if ((p.additives_tags || []).length > 10) s -= 10;
  return Math.max(5, Math.min(95, s));
}

function getScoreInfo(score) {
  if (score >= 75) return { color: "#27ae60", label: "Shkëlqyer", emoji: "🌿" };
  if (score >= 50) return { color: "#f1c40f", label: "Mirë", emoji: "😊" };
  if (score >= 25) return { color: "#e67e22", label: "Mesatar", emoji: "😐" };
  return { color: "#e74c3c", label: "Keq", emoji: "⚠️" };
}

export default function CosmeticsScreen() {
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
      let data = null;
      const res1 = await fetch(`https://world.openbeautyfacts.org/api/v0/product/${c}.json`);
      const d1 = await res1.json();
      if (d1.status === 1) data = d1.product;
      else {
        const res2 = await fetch(`https://world.openfoodfacts.org/api/v0/product/${c}.json`);
        const d2 = await res2.json();
        if (d2.status === 1) data = d2.product;
      }
      if (data) setProduct(data);
      else setError("Produkti nuk u gjet. Provoni një barkod tjetër.");
    } catch {
      setError("Gabim në lidhjen me serverin.");
    } finally {
      setLoading(false);
    }
  }

  const score = product ? computeCosmeticScore(product) : null;
  const info = score !== null ? getScoreInfo(score) : null;
  const badIngredients = product
    ? DANGEROUS.filter(d => (product.ingredients_text || "").toLowerCase().includes(d.key))
    : [];

  return (
    <ScrollView style={styles.container} contentContainerStyle={{ paddingBottom: 32 }}>
      <View style={[styles.header, { backgroundColor: ACCENT }]}>
        <Text style={styles.headerTitle}>✨ Kozmetikë</Text>
        <Text style={styles.headerSub}>Zbuloni përbërësit e rrezikshëm</Text>
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

            <View style={[styles.card, badIngredients.length ? { backgroundColor: "#fff5f5", borderLeftWidth: 4, borderLeftColor: "#e74c3c" } : { backgroundColor: "#f0fff4", borderLeftWidth: 4, borderLeftColor: "#27ae60" }]}>
              {badIngredients.length ? (
                <>
                  <Text style={[styles.sectionTitle, { color: "#e74c3c" }]}>⚠ Përbërës të rrezikshëm</Text>
                  {badIngredients.map(b => (
                    <View key={b.key} style={styles.dangerRow}>
                      <View style={styles.dangerBadge}><Text style={styles.dangerBadgeText}>{b.label}</Text></View>
                      <Text style={styles.dangerRisk}>{b.risk}</Text>
                    </View>
                  ))}
                </>
              ) : (
                <Text style={{ color: "#27ae60", fontWeight: "700" }}>✓ Asnjë përbërës i rrezikshëm i zbuluar</Text>
              )}
            </View>

            {product.ingredients_text && (
              <View style={styles.card}>
                <Text style={styles.sectionTitle}>Përbërësit</Text>
                <View style={{ flexDirection: "row", flexWrap: "wrap", gap: 6 }}>
                  {product.ingredients_text.split(/,|;/).map((ing, i) => {
                    const t = ing.trim();
                    if (!t) return null;
                    const isDanger = DANGEROUS.some(d => t.toLowerCase().includes(d.key));
                    return (
                      <View key={i} style={[styles.ingBadge, isDanger && styles.ingBadgeDanger]}>
                        <Text style={[styles.ingText, isDanger && styles.ingTextDanger]}>{t}</Text>
                      </View>
                    );
                  })}
                </View>
              </View>
            )}

            {product.allergens && product.allergens.length > 0 && (
              <View style={[styles.card, { backgroundColor: "#fff5f5", borderLeftWidth: 4, borderLeftColor: "#e74c3c" }]}>
                <Text style={[styles.sectionTitle, { color: "#e74c3c" }]}>⚠ Alergjenët</Text>
                <Text style={{ color: "#555" }}>{product.allergens.replace(/en:/g, "")}</Text>
              </View>
            )}

            {(product.additives_tags || []).length > 0 && (
              <View style={styles.card}>
                <Text style={styles.sectionTitle}>Aditivë</Text>
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
  container: { flex: 1, backgroundColor: "#fdf4f8" },
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
  dangerRow: { flexDirection: "row", alignItems: "center", gap: 10, marginBottom: 8 },
  dangerBadge: { backgroundColor: "#e74c3c", paddingHorizontal: 8, paddingVertical: 3, borderRadius: 6 },
  dangerBadgeText: { color: "#fff", fontSize: 11, fontWeight: "700" },
  dangerRisk: { color: "#555", fontSize: 13, flex: 1 },
  ingBadge: { backgroundColor: "#f0f0f0", paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  ingBadgeDanger: { backgroundColor: "#e74c3c" },
  ingText: { fontSize: 11, color: "#555" },
  ingTextDanger: { color: "#fff", fontWeight: "600" },
  errorCard: { backgroundColor: "#fff0f0", borderRadius: 12, padding: 16, borderLeftWidth: 4, borderLeftColor: "#e74c3c" },
  errorText: { color: "#e74c3c", fontWeight: "600" },
  additiveBadge: { backgroundColor: "#f0f0f0", paddingHorizontal: 8, paddingVertical: 4, borderRadius: 6 },
  additiveText: { fontSize: 11, color: "#555", fontFamily: "monospace" },
});
