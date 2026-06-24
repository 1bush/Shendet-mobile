import React, { useState } from 'react';
import {
  View, Text, TextInput, TouchableOpacity, ScrollView,
  StyleSheet, SafeAreaView, ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';

interface MedInfo {
  emri: string;
  lloji: string;
  perdorimi: string;
  dozimi: string;
  efekte_anesore: string[];
  kujdes: string[];
  nderveprime: string;
  ekuivalente: string;
}

const SHEMBUJ = ['Paracetamol', 'Ibuprofen', 'Amoxicilin', 'Omeprazol', 'Metformin'];

export default function ModuliBarnas() {
  const [kerkimi, setKerkimi] = useState('');
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<MedInfo | null>(null);
  const [error, setError] = useState('');

  const kerko = async (emri?: string) => {
    const query = emri || kerkimi.trim();
    if (!query) return;
    setLoading(true);
    setResult(null);
    setError('');

    try {
      const res = await fetch('https://api.anthropic.com/v1/messages', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'x-api-key': process.env.EXPO_PUBLIC_ANTHROPIC_API_KEY || '',
          'anthropic-version': '2023-06-01',
        },
        body: JSON.stringify({
          model: 'claude-sonnet-4-6',
          max_tokens: 1024,
          messages: [{
            role: 'user',
            content: `Jep informacion bazë për ilaçin/barnën: "${query}"

Kthe VETËM JSON (pa asgjë tjetër):
{
  "emri": "Emri i plotë i ilaçit",
  "lloji": "Lloji (analgjezik, antibiotik, etj)",
  "perdorimi": "Për çfarë përdoret (1-2 fjali)",
  "dozimi": "Dozimi tipik për të rritur",
  "efekte_anesore": ["efekt 1", "efekt 2", "efekt 3"],
  "kujdes": ["kujdes 1", "kujdes 2"],
  "nderveprime": "Ndërveprimet kryesore me ilaçe tjera (1 fjali)",
  "ekuivalente": "Alternativa/ekuivalente të zakonshme shqiptare"
}

Nëse nuk gjen ilaçin, kthe {"emri": "", "gabim": "Ilaçi nuk u gjet"}`
          }],
        }),
      });
      const data = await res.json();
      const text = data.content?.[0]?.text || '{}';
      const parsed = JSON.parse(text.replace(/```json|```/g, '').trim());
      if (parsed.gabim || !parsed.emri) {
        setError('Ilaçi nuk u gjet. Provo një emër tjetër.');
      } else {
        setResult(parsed);
      }
    } catch {
      setError('Gabim në lidhje. Provo përsëri.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView showsVerticalScrollIndicator={false} keyboardShouldPersistTaps="handled">
        <View style={styles.header}>
          <Text style={styles.title}>Barnat & Ilaçet</Text>
          <Text style={styles.sub}>Kërko informacion për çdo ilaç</Text>
        </View>

        {/* Disclaimer */}
        <View style={styles.disclaimer}>
          <Ionicons name="warning-outline" size={16} color="#92400E" />
          <Text style={styles.disclaimerText}>
            Informacioni është vetëm edukativ. Konsulto gjithmonë mjekun ose farmacistin.
          </Text>
        </View>

        {/* Search */}
        <View style={styles.searchBox}>
          <View style={styles.searchRow}>
            <Ionicons name="search-outline" size={20} color="#6B7280" />
            <TextInput
              style={styles.searchInput}
              value={kerkimi}
              onChangeText={setKerkimi}
              placeholder="Emri i ilaçit (p.sh. Paracetamol)"
              placeholderTextColor="#9CA3AF"
              onSubmitEditing={() => kerko()}
              returnKeyType="search"
            />
            {kerkimi.length > 0 && (
              <TouchableOpacity onPress={() => { setKerkimi(''); setResult(null); }}>
                <Ionicons name="close-circle" size={20} color="#9CA3AF" />
              </TouchableOpacity>
            )}
          </View>
          <TouchableOpacity style={styles.searchBtn} onPress={() => kerko()} disabled={!kerkimi.trim() || loading}>
            {loading
              ? <ActivityIndicator size="small" color="white" />
              : <Text style={styles.searchBtnText}>Kërko</Text>}
          </TouchableOpacity>
        </View>

        {/* Quick examples */}
        {!result && !loading && (
          <View style={styles.examplesBox}>
            <Text style={styles.examplesLabel}>Kërko shpejt:</Text>
            <View style={styles.examplesRow}>
              {SHEMBUJ.map(s => (
                <TouchableOpacity key={s} style={styles.exampleChip}
                  onPress={() => { setKerkimi(s); kerko(s); }}>
                  <Text style={styles.exampleText}>{s}</Text>
                </TouchableOpacity>
              ))}
            </View>
          </View>
        )}

        {error ? (
          <View style={styles.errorBox}>
            <Ionicons name="alert-circle-outline" size={20} color="#EF4444" />
            <Text style={styles.errorText}>{error}</Text>
          </View>
        ) : null}

        {result && (
          <View style={styles.resultBox}>
            {/* Header */}
            <View style={styles.medHeader}>
              <View style={styles.medIcon}>
                <Ionicons name="medical" size={28} color="white" />
              </View>
              <View style={{ flex: 1 }}>
                <Text style={styles.medName}>{result.emri}</Text>
                <Text style={styles.medType}>{result.lloji}</Text>
              </View>
            </View>

            <InfoSection icon="information-circle-outline" label="Përdorimi" color="#3B82F6">
              <Text style={styles.infoText}>{result.perdorimi}</Text>
            </InfoSection>

            <InfoSection icon="timer-outline" label="Dozimi tipik" color="#8B5CF6">
              <Text style={styles.infoText}>{result.dozimi}</Text>
            </InfoSection>

            <InfoSection icon="alert-circle-outline" label="Efekte anësore" color="#F59E0B">
              {result.efekte_anesore.map((e, i) => (
                <View key={i} style={styles.bulletRow}>
                  <View style={styles.bullet} />
                  <Text style={styles.bulletText}>{e}</Text>
                </View>
              ))}
            </InfoSection>

            <InfoSection icon="shield-outline" label="Kujdes" color="#EF4444">
              {result.kujdes.map((k, i) => (
                <View key={i} style={styles.bulletRow}>
                  <View style={[styles.bullet, { backgroundColor: '#EF4444' }]} />
                  <Text style={styles.bulletText}>{k}</Text>
                </View>
              ))}
            </InfoSection>

            <InfoSection icon="git-merge-outline" label="Ndërveprime" color="#6B7280">
              <Text style={styles.infoText}>{result.nderveprime}</Text>
            </InfoSection>

            {result.ekuivalente && (
              <InfoSection icon="swap-horizontal-outline" label="Ekuivalente shqiptare" color="#1D9E75">
                <Text style={styles.infoText}>{result.ekuivalente}</Text>
              </InfoSection>
            )}

            <View style={styles.disclaimer}>
              <Ionicons name="warning-outline" size={14} color="#92400E" />
              <Text style={[styles.disclaimerText, { fontSize: 11 }]}>
                Ky informacion është i përgjithshëm. Doza dhe trajtimi duhet të caktohen nga mjeku ose farmacisti.
              </Text>
            </View>
          </View>
        )}

        <View style={{ height: 32 }} />
      </ScrollView>
    </SafeAreaView>
  );
}

function InfoSection({ icon, label, color, children }: any) {
  return (
    <View style={sStyles.section}>
      <View style={sStyles.sectionHeader}>
        <Ionicons name={icon} size={16} color={color} />
        <Text style={[sStyles.sectionLabel, { color }]}>{label}</Text>
      </View>
      <View style={sStyles.sectionBody}>{children}</View>
    </View>
  );
}

const sStyles = StyleSheet.create({
  section: { marginBottom: 12 },
  sectionHeader: { flexDirection: 'row', alignItems: 'center', gap: 6, marginBottom: 6 },
  sectionLabel: { fontSize: 12, fontWeight: '700', textTransform: 'uppercase', letterSpacing: 0.5 },
  sectionBody: { paddingLeft: 4 },
});

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F9FAFB' },
  header: { padding: 20, backgroundColor: 'white', borderBottomWidth: 1, borderBottomColor: '#E5E7EB' },
  title: { fontSize: 22, fontWeight: '700', color: '#111827' },
  sub: { fontSize: 13, color: '#6B7280', marginTop: 2 },
  disclaimer: { flexDirection: 'row', gap: 8, backgroundColor: '#FEF3C7',
    padding: 12, margin: 16, borderRadius: 10, alignItems: 'flex-start' },
  disclaimerText: { flex: 1, fontSize: 12, color: '#92400E', lineHeight: 18 },
  searchBox: { margin: 16, backgroundColor: 'white', borderRadius: 16,
    padding: 16, gap: 12, borderWidth: 1, borderColor: '#E5E7EB' },
  searchRow: { flexDirection: 'row', alignItems: 'center', gap: 8,
    backgroundColor: '#F3F4F6', borderRadius: 10, paddingHorizontal: 12, paddingVertical: 8 },
  searchInput: { flex: 1, fontSize: 15, color: '#111827' },
  searchBtn: { backgroundColor: '#1D9E75', borderRadius: 10,
    paddingVertical: 12, alignItems: 'center' },
  searchBtnText: { color: 'white', fontWeight: '600', fontSize: 15 },
  examplesBox: { paddingHorizontal: 16, marginBottom: 8 },
  examplesLabel: { fontSize: 12, color: '#6B7280', marginBottom: 8 },
  examplesRow: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  exampleChip: { backgroundColor: 'white', borderWidth: 1, borderColor: '#E5E7EB',
    borderRadius: 20, paddingHorizontal: 12, paddingVertical: 6 },
  exampleText: { fontSize: 13, color: '#374151' },
  errorBox: { flexDirection: 'row', gap: 8, alignItems: 'center',
    margin: 16, padding: 14, backgroundColor: '#FEF2F2', borderRadius: 10 },
  errorText: { color: '#EF4444', fontSize: 14 },
  resultBox: { margin: 16, backgroundColor: 'white', borderRadius: 16, padding: 16,
    borderWidth: 1, borderColor: '#E5E7EB' },
  medHeader: { flexDirection: 'row', alignItems: 'center', gap: 12, marginBottom: 16,
    paddingBottom: 16, borderBottomWidth: 1, borderBottomColor: '#E5E7EB' },
  medIcon: { width: 52, height: 52, borderRadius: 14, backgroundColor: '#EF4444',
    alignItems: 'center', justifyContent: 'center' },
  medName: { fontSize: 20, fontWeight: '700', color: '#111827' },
  medType: { fontSize: 13, color: '#6B7280', marginTop: 2 },
  infoText: { fontSize: 14, color: '#374151', lineHeight: 20 },
  bulletRow: { flexDirection: 'row', alignItems: 'flex-start', gap: 8, marginBottom: 4 },
  bullet: { width: 6, height: 6, borderRadius: 3, backgroundColor: '#F59E0B', marginTop: 6 },
  bulletText: { flex: 1, fontSize: 14, color: '#374151', lineHeight: 20 },
});
