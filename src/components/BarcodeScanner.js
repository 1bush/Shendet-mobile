import React, { useState, useEffect } from "react";
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { CameraView, useCameraPermissions } from "expo-camera";

export default function BarcodeScanner({ onScan, onClose, accent = "#1a7a4a" }) {
  const [permission, requestPermission] = useCameraPermissions();
  const [scanned, setScanned] = useState(false);

  useEffect(() => {
    if (!permission?.granted) requestPermission();
  }, []);

  function handleBarcode({ data }) {
    if (scanned) return;
    setScanned(true);
    onScan(data);
  }

  if (!permission) return <View style={styles.center}><Text>Duke hapur kamerën...</Text></View>;
  if (!permission.granted) return (
    <View style={styles.center}>
      <Text style={styles.permText}>Leje e kamerës e nevojshme</Text>
      <TouchableOpacity style={[styles.btn, { backgroundColor: accent }]} onPress={requestPermission}>
        <Text style={styles.btnText}>Jep leje</Text>
      </TouchableOpacity>
    </View>
  );

  return (
    <View style={styles.container}>
      <CameraView
        style={StyleSheet.absoluteFill}
        facing="back"
        onBarcodeScanned={handleBarcode}
        barcodeScannerSettings={{ barcodeTypes: ["ean13","ean8","upc_a","upc_e","code128","code39"] }}
      />
      <View style={styles.overlay}>
        <View style={styles.frame} />
        <Text style={styles.hint}>Vendosni barkodin brenda kornizës</Text>
        <TouchableOpacity style={styles.closeBtn} onPress={onClose}>
          <Text style={styles.closeTxt}>✕ Mbyll</Text>
        </TouchableOpacity>
        {scanned && (
          <TouchableOpacity style={[styles.btn, { backgroundColor: accent }]} onPress={() => setScanned(false)}>
            <Text style={styles.btnText}>Skano përsëri</Text>
          </TouchableOpacity>
        )}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 },
  center: { flex: 1, alignItems: "center", justifyContent: "center", gap: 16, padding: 24 },
  overlay: { flex: 1, alignItems: "center", justifyContent: "center", gap: 20, backgroundColor: "rgba(0,0,0,0.4)" },
  frame: { width: 260, height: 160, borderRadius: 12, borderWidth: 3, borderColor: "#fff" },
  hint: { color: "#fff", fontSize: 14 },
  closeBtn: { position: "absolute", top: 50, right: 20, backgroundColor: "rgba(0,0,0,0.5)", padding: 10, borderRadius: 10 },
  closeTxt: { color: "#fff", fontWeight: "700" },
  btn: { paddingHorizontal: 24, paddingVertical: 12, borderRadius: 12 },
  btnText: { color: "#fff", fontWeight: "700" },
  permText: { fontSize: 16, color: "#555", textAlign: "center" },
});
