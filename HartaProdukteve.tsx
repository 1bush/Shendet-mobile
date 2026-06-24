import React, { useState } from 'react';
import {
  View, Text, TouchableOpacity, ScrollView,
  StyleSheet, SafeAreaView, TextInput,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';

type Kategoria = 'te_gjitha' | 'fermere' | 'organike' | 'bulmetore' | 'mish' | 'fruta_perime';

interface Prodhuesi {
  id: string;
  emri: string;
  kategoria: Kategoria;
  produktet: string[];
  vendndodhja: string;
  distanca: string;
  ikona: string;
  organik: boolean;
  disponueshmeria: string;
}

const PRODHUESIT: Prodhuesi[] = [
  { id: '1', emri: 'Ferma Petrela', kategoria: 'fermere', produktet: ['Domate', 'Tranguj', 'Speca'],
    vendndodhja: 'Petrela, Tiranë', distanca: '18 km', ikona: '🌿', organik: true,
    disponueshmeria: 'Çdo të shtunë, Tregu i Gjelbër' },
  { id: '2', emri: 'Djathëtorja Shkodër', kategoria: 'bulmetore', produktet: ['Djathë i bardhë', 'Gjizë', 'Kos'],
    vendndodhja: 'Shkodër', distanca: '110 km', ikona: '🧀', organik: false,
    disponueshmeria: 'Online & dyqane partnerë' },
  { id: '3', emri: 'Pemëtaria Korçë', kategoria: 'fruta_perime', produktet: ['Mollë', 'Dardhë', 'Vishnje'],
    vendndodhja: 'Korçë', distanca: '180 km', ikona: '🍎', organik: true,
    disponueshmeria: 'Shtator - Nëntor' },
  { id: '4', emri: 'Mjalti i Lurës', kategoria: 'organike', produktet: ['Mjaltë lule', 'Mjaltë gështenje', 'Polen'],
    vendndodhja: 'Lurë, Dibër', distanca: '95 km', ikona: '🍯', organik: true,
    disponueshmeria: 'Gjatë gjithë vitit' },
  { id: '5', emri: 'Blegtoria Dardha', kategoria: 'mish', produktet: ['Qengji i Dardhas', 'Djathë dhie'],
    vendndodhja: 'Dardha, Korçë', distanca: '190 km', ikona: '🐑', organik: true,
    disponueshmeria: 'Me porosi' },
  { id: '6', emri: 'Vreshtat Berat', kategoria: 'fermere', produktet: ['Rrush', 'Vaj ulliri', 'Pasta domatesh'],
    vendndodhja: 'Berat', distanca: '120 km', ikona: '🍇', organik: false,
    disponueshmeria: 'Gusht - Tetor' },
  { id: '7', emri: 'Agro Fier', kategoria: 'fruta_perime', produktet: ['Shalqi', 'Pjepër', 'Qepë', 'Hudhër'],
    vendndodhja: 'Fier', distanca: '130 km', ikona: '🌾', organik: false,
    disponueshmeria: 'Qershor - Shtator' },
  { id: '8', emri: 'Bio Farm Shqipëri', kategoria: 'organike', produktet: ['Drithëra bio', 'Miell guri', 'Fasulje'],
    vendndodhja: 'Elbasan', distanca: '50 km', ikona: '🌱', organik: true,
    disponueshmeria: 'Gjatë gjithë vitit, online' },
  { id: '9', emri: 'Peshkimi Durrës', kategoria: 'mish', produktet: ['Peshk i freskët', 'Karkaleca', 'Midhje'],
    vendndodhja: 'Durrës', distanca: '38 km', ikona: '🐟', organik: false,
    disponueshmeria: 'Çdo ditë, mëngjes' },
  { id: '10', emri: 'Ulliri Vlora', kategoria: 'organike', produktet: ['Vaj ulliri ekstra', 'Ullinj turshi'],
    vendndodhja: 'Vlorë', distanca: '150 km', ikona: '🫒', organik: true,
    disponueshmeria: 'Tetor - Dhjetor' },
];

const KATEGORITE = [
  { id: 'te_gjitha', label: 'Të gjitha', ikona: 'apps-outline' },
  { id: 'fermere', label: 'Fermerë', ikona: 'leaf-outline' },
  { id: 'organike', label: 'Organik', ikona: 'flower-outline' },
  { id: 'bulmetore', label: 'Bulmetore', ikona: 'water-outline' },
  { id: 'mish', label: 'Mish/Peshk', ikona: 'fish-outline' },
  { id: 'fruta_perime', label: 'Fruta & Perime', ikona: 'nutrition-outline' },
];

export default function HartaProdukteve() {
  const [kategoria, setKategoria] = useState<Kategoria>('te_gjitha');
  const [kerkimi, setKerkimi] = useState('');
  const [zgjedhur, setZgjedhur] = useState<Prodhuesi | null>(null);

  const filtered = PRODHUESIT.filter(p => {
    const matchKat = kategoria === 'te_gjitha' || p.kategoria === kategoria;
    const matchKer = !kerkimi || p.emri.toLowerCase().includes(kerkimi.toLowerCase()) ||
      p.produktet.some(pr => pr.toLowerCase().includes(kerkimi.toLowerCase())) ||
      p.vendndodhja.toLowerCase().includes(kerkimi.toLowerCase());
    return matchKat && matchKer;
  });

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Produktet Lokale</Text>
        <Text style={styles.sub}>Prodhues shqiptarë · {filtered.length} rezultate</Text>
      </View>

      {/* Search */}
      <View style={styles.searchRow}>
        <Ionicons name="search-outline" size={18} color="#6B7280" />
        <TextInput
          style={styles.searchInput}
          value={kerkimi}
          onChangeText={setKerkimi}
          placeholder="Kërko prodhues ose produkt..."
          placeholderTextColor="#9CA3AF"
        />
        {kerkimi.length > 0 && (
          <TouchableOpacity onPress={() => setKerkimi('')}>
            <Ionicons name="close-circle" size={18} color="#9CA3AF" />
          </TouchableOpacity>
        )}
      </View>

      {/* Category filter */}
      <ScrollView horizontal showsHorizontalScrollIndicator={false}
        style={styles.katRow} contentContainerStyle={{ paddingHorizontal: 16, gap: 8, paddingVertical: 10 }}>
        {KATEGORITE.map(k => (
          <TouchableOpacity key={k.id} style={[styles.katChip, kategoria === k.id && styles.katChipActive]}
            onPress={() => setKategoria(k.id as Kategoria)}>
            <Ionicons name={k.ikona as any} size={14} color={kategoria === k.id ? 'white' : '#6B7280'} />
            <Text style={[styles.katLabel, kategoria === k.id && styles.katLabelActive]}>{k.label}</Text>
          </TouchableOpacity>
        ))}
      </ScrollView>

      {/* Detail modal */}
      {zgjedhur && (
        <View style={styles.detailOverlay}>
          <TouchableOpacity style={styles.detailBg} onPress={() => setZgjedhur(null)} />
          <View style={styles.detailCard}>
            <View style={styles.detailHeader}>
              <Text style={styles.detailIcon}>{zgjedhur.ikona}</Text>
              <View style={{ flex: 1 }}>
                <Text style={styles.detailName}>{zgjedhur.emri}</Text>
                <Text style={styles.detailLoc}>
                  <Ionicons name="location-outline" size={12} color="#6B7280" /> {zgjedhur.vendndodhja}
                </Text>
              </View>
              {zgjedhur.organik && (
                <View style={styles.organicBadge}>
                  <Text style={styles.organicBadgeText}>BIO</Text>
                </View>
              )}
              <TouchableOpacity onPress={() => setZgjedhur(null)} style={{ padding: 4 }}>
                <Ionicons name="close" size={22} color="#6B7280" />
              </TouchableOpacity>
            </View>
            <View style={styles.detailRow}>
              <Ionicons name="navigate-outline" size={16} color="#1D9E75" />
              <Text style={styles.detailMeta}>{zgjedhur.distanca} nga Tirana</Text>
            </View>
            <View style={styles.detailRow}>
              <Ionicons name="time-outline" size={16} color="#1D9E75" />
              <Text style={styles.detailMeta}>{zgjedhur.disponueshmeria}</Text>
            </View>
            <Text style={styles.produktetLabel}>Produktet:</Text>
            <View style={styles.produktetRow}>
              {zgjedhur.produktet.map((p, i) => (
                <View key={i} style={styles.produktChip}>
                  <Text style={styles.produktChipText}>{p}</Text>
                </View>
              ))}
            </View>
          </View>
        </View>
      )}

      <ScrollView contentContainerStyle={{ padding: 16, paddingBottom: 32 }}>
        {filtered.length === 0 ? (
          <View style={styles.empty}>
            <Text style={{ fontSize: 40 }}>🌾</Text>
            <Text style={styles.emptyText}>Nuk u gjet asgjë</Text>
          </View>
        ) : (
          filtered.map(p => (
            <TouchableOpacity key={p.id} style={styles.card} onPress={() => setZgjedhur(p)} activeOpacity={0.8}>
              <View style={styles.cardLeft}>
                <Text style={styles.cardIcon}>{p.ikona}</Text>
              </View>
              <View style={styles.cardContent}>
                <View style={styles.cardTopRow}>
                  <Text style={styles.cardName}>{p.emri}</Text>
                  {p.organik && <View style={styles.bioTag}><Text style={styles.bioTagText}>BIO</Text></View>}
                </View>
                <Text style={styles.cardLoc}>
                  <Ionicons name="location-outline" size={11} color="#6B7280" /> {p.vendndodhja} · {p.distanca}
                </Text>
                <Text style={styles.cardProd} numberOfLines={1}>
                  {p.produktet.join(' · ')}
                </Text>
              </View>
              <Ionicons name="chevron-forward" size={16} color="#D1D5DB" />
            </TouchableOpacity>
          ))
        )}
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F9FAFB' },
  header: { padding: 20, backgroundColor: 'white', borderBottomWidth: 1, borderBottomColor: '#E5E7EB' },
  title: { fontSize: 22, fontWeight: '700', color: '#111827' },
  sub: { fontSize: 13, color: '#6B7280', marginTop: 2 },
  searchRow: { flexDirection: 'row', alignItems: 'center', gap: 8,
    backgroundColor: 'white', paddingHorizontal: 16, paddingVertical: 10,
    borderBottomWidth: 1, borderBottomColor: '#E5E7EB' },
  searchInput: { flex: 1, fontSize: 14, color: '#111827' },
  katRow: { backgroundColor: 'white', borderBottomWidth: 1, borderBottomColor: '#E5E7EB' },
  katChip: { flexDirection: 'row', alignItems: 'center', gap: 5,
    borderRadius: 20, paddingHorizontal: 12, paddingVertical: 6,
    backgroundColor: '#F3F4F6', borderWidth: 1, borderColor: '#E5E7EB' },
  katChipActive: { backgroundColor: '#1D9E75', borderColor: '#1D9E75' },
  katLabel: { fontSize: 12, color: '#6B7280', fontWeight: '500' },
  katLabelActive: { color: 'white' },
  card: { flexDirection: 'row', alignItems: 'center', backgroundColor: 'white',
    borderRadius: 14, padding: 14, marginBottom: 8, borderWidth: 1, borderColor: '#E5E7EB', gap: 12 },
  cardLeft: { width: 44, height: 44, borderRadius: 12, backgroundColor: '#ECFDF5',
    alignItems: 'center', justifyContent: 'center' },
  cardIcon: { fontSize: 24 },
  cardContent: { flex: 1 },
  cardTopRow: { flexDirection: 'row', alignItems: 'center', gap: 6, marginBottom: 2 },
  cardName: { fontSize: 14, fontWeight: '600', color: '#111827' },
  bioTag: { backgroundColor: '#ECFDF5', borderRadius: 4, paddingHorizontal: 5, paddingVertical: 1 },
  bioTagText: { fontSize: 10, fontWeight: '700', color: '#1D9E75' },
  cardLoc: { fontSize: 11, color: '#6B7280', marginBottom: 2 },
  cardProd: { fontSize: 12, color: '#9CA3AF' },
  empty: { alignItems: 'center', paddingTop: 80, gap: 12 },
  emptyText: { fontSize: 16, color: '#6B7280' },
  detailOverlay: { position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, zIndex: 50 },
  detailBg: { position: 'absolute', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(0,0,0,0.4)' },
  detailCard: { position: 'absolute', bottom: 0, left: 0, right: 0,
    backgroundColor: 'white', borderTopLeftRadius: 24, borderTopRightRadius: 24, padding: 24 },
  detailHeader: { flexDirection: 'row', alignItems: 'center', gap: 12, marginBottom: 16 },
  detailIcon: { fontSize: 36 },
  detailName: { fontSize: 18, fontWeight: '700', color: '#111827' },
  detailLoc: { fontSize: 12, color: '#6B7280', marginTop: 2 },
  organicBadge: { backgroundColor: '#ECFDF5', borderRadius: 6, paddingHorizontal: 8, paddingVertical: 3 },
  organicBadgeText: { fontSize: 11, fontWeight: '700', color: '#1D9E75' },
  detailRow: { flexDirection: 'row', alignItems: 'center', gap: 8, marginBottom: 8 },
  detailMeta: { fontSize: 14, color: '#374151' },
  produktetLabel: { fontSize: 12, fontWeight: '600', color: '#6B7280', marginTop: 8, marginBottom: 6 },
  produktetRow: { flexDirection: 'row', flexWrap: 'wrap', gap: 6 },
  produktChip: { backgroundColor: '#F3F4F6', borderRadius: 20, paddingHorizontal: 10, paddingVertical: 4 },
  produktChipText: { fontSize: 13, color: '#374151' },
});
