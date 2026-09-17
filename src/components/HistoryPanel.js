/**
 * HistoryPanel — shfaq historikun lokal të kërkimeve.
 *
 * Përdorimi:
 *   <HistoryPanel type="food" accent="#1a7a4a" />
 *
 * Të dhënat vijnë nga `src/utils/storage.js` (AsyncStorage),
 * prandaj punon plotësisht offline. Rifreskohet sa herë ekrani merr fokus.
 */

import React, { useCallback, useState } from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { useFocusEffect } from "@react-navigation/native";
import { getHistory, clearHistory, formatoDate } from "../utils/storage";

export default function HistoryPanel({ type = "food", accent = "#1a7a4a" }) {
  const [zera, setZera] = useState([]);
  const [hapur, setHapur] = useState(false);

  const ringarko = useCallback(() => {
    let aktiv = true;
    getHistory(type).then((h) => {
      if (aktiv) setZera(Array.isArray(h) ? h : []);
    });
    return () => {
      aktiv = false;
    };
  }, [type]);

  // Rifresko historikun sa herë hapet ekrani.
  useFocusEffect(ringarko);

  const pastro = async () => {
    await clearHistory(type);
    setZera([]);
  };

  if (zera.length === 0) return null;

  // Ngjyra e pikëve sipas skorit (të njëjtat kufij si në ekrane).
  const ngjyraSkor = (s) =>
    s >= 75 ? "#27ae60" : s >= 50 ? "#f1c40f" : s >= 25 ? "#e67e22" : "#e74c3c";

  const shfaqur = hapur ? zera : zera.slice(0, 3);

  return (
    <View style={styles.card}>
      <TouchableOpacity
        style={styles.header}
        activeOpacity={0.8}
        onPress={() => setHapur((h) => !h)}
      >
        <View>
          <Text style={[styles.title, { color: accent }]}> Historiku</Text>
          <Text style={styles.subtitle}>
            {zera.length} {zera.length === 1 ? "kërkim i ruajtur" : "kërkime të ruajtura"}
          </Text>
        </View>
        <Text style={styles.chevron}>{hapur ? "▲" : "▼"}</Text>
      </TouchableOpacity>

      <View style={{ paddingHorizontal: 16, paddingBottom: 12 }}>
        {shfaqur.map((z) => (
          <View key={z.id} style={styles.row}>
            <View
              style={[
                styles.dot,
                { backgroundColor: ngjyraSkor(Number(z.score) || 0) },
              ]}
            >
              <Text style={styles.dotText}>{z.score ?? "–"}</Text>
            </View>
            <View style={{ flex: 1 }}>
              <Text style={styles.name} numberOfLines={1}>
                {z.name || "Pa emër"}
              </Text>
              <Text style={styles.meta}>
                {z.barcode ? `${z.barcode} · ` : ""}
                {formatoDate(z.date)}
              </Text>
            </View>
          </View>
        ))}

        <View style={styles.actions}>
          {zera.length > 3 && (
            <TouchableOpacity onPress={() => setHapur((h) => !h)}>
              <Text style={[styles.actionText, { color: accent }]}>
                {hapur ? "Shfaq më pak" : `Shfaq të gjitha (${zera.length})`}
              </Text>
            </TouchableOpacity>
          )}
          <TouchableOpacity onPress={pastro}>
            <Text style={styles.clearText}>Pastro historikun</Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: "#fff",
    borderRadius: 16,
    paddingTop: 4,
    shadowColor: "#000",
    shadowOpacity: 0.06,
    shadowRadius: 8,
    elevation: 2,
  },
  header: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    padding: 16,
  },
  title: { fontSize: 14, fontWeight: "800" },
  subtitle: { fontSize: 11, color: "#888", marginTop: 2 },
  chevron: { fontSize: 12, color: "#888" },
  row: { flexDirection: "row", alignItems: "center", gap: 10, paddingVertical: 8 },
  dot: {
    width: 38,
    height: 38,
    borderRadius: 19,
    alignItems: "center",
    justifyContent: "center",
  },
  dotText: { color: "#fff", fontWeight: "800", fontSize: 13 },
  name: { fontSize: 14, fontWeight: "600", color: "#333" },
  meta: { fontSize: 11, color: "#999", marginTop: 2 },
  actions: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginTop: 10,
    paddingTop: 10,
    borderTopWidth: 1,
    borderTopColor: "#f0f0f0",
  },
  actionText: { fontSize: 12, fontWeight: "700" },
  clearText: { fontSize: 12, color: "#e74c3c", fontWeight: "600" },
});