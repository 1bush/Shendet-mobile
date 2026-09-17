/**
 * Menu — ekrani "Më shumë".
 *
 * Aplikacioni ka 5 tab-e të dukshme; modulet dytësore hapen nga këtu
 * përmes `navigation.navigate(<emri i tab-it>)`, sepse janë të regjistruara
 * në App.js me `tabBarButton: () => null`.
 */

import React from 'react';
import {
  View, Text, TouchableOpacity, ScrollView, StyleSheet, SafeAreaView,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useNavigation } from '@react-navigation/native';

const KARTA = [
  {
    tab: 'Faturë',
    titull: 'Skanim Fature',
    pershkrim: 'Foto faturën dhe merr vlerësim shëndetësor',
    ikona: 'receipt-outline',
    ngjyra: '#1D9E75',
  },
  {
    tab: 'Barnat',
    titull: 'Barnat & Ilaçet',
    pershkrim: 'Informacion bazë dhe kujdes për ilaçet',
    ikona: 'medkit-outline',
    ngjyra: '#EF4444',
  },
  {
    tab: 'Harta',
    titull: 'Harta e Prodhuesve',
    pershkrim: 'Prodhues vendorë dhe produkte organike',
    ikona: 'map-outline',
    ngjyra: '#F59E0B',
  },
  {
    tab: 'Profili',
    titull: 'Profili Familjar',
    pershkrim: 'Alergji dhe dieta për çdo anëtar',
    ikona: 'people-outline',
    ngjyra: '#4a2d8a',
  },
];

export default function Menu() {
  const navigation = useNavigation<any>();

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView contentContainerStyle={{ padding: 16, paddingBottom: 32 }}>
        <View style={styles.header}>
          <Text style={styles.title}>Më shumë</Text>
          <Text style={styles.sub}>Modulet shtesë të SHËNDETI-IM</Text>
        </View>

        {KARTA.map((k) => (
          <TouchableOpacity
            key={k.tab}
            style={styles.card}
            activeOpacity={0.8}
            onPress={() => navigation.navigate(k.tab)}
          >
            <View style={[styles.iconBox, { backgroundColor: k.ngjyra }]}>
              <Ionicons name={k.ikona as any} size={22} color="white" />
            </View>
            <View style={{ flex: 1 }}>
              <Text style={styles.cardTitle}>{k.titull}</Text>
              <Text style={styles.cardSub}>{k.pershkrim}</Text>
            </View>
            <Ionicons name="chevron-forward" size={18} color="#D1D5DB" />
          </TouchableOpacity>
        ))}

        <View style={styles.infoBox}>
          <Ionicons name="sparkles-outline" size={16} color="#1D9E75" />
          <Text style={styles.infoText}>
            Asistenti AI punon me Ollama lokale. Nëse AI nuk përgjigjet, shih
            README.md → «AI lokale (Ollama)».
          </Text>
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F9FAFB' },
  header: { marginBottom: 16 },
  title: { fontSize: 22, fontWeight: '700', color: '#111827' },
  sub: { fontSize: 13, color: '#6B7280', marginTop: 2 },
  card: {
    flexDirection: 'row', alignItems: 'center', gap: 12,
    backgroundColor: 'white', borderRadius: 14, padding: 14,
    marginBottom: 10, borderWidth: 1, borderColor: '#E5E7EB',
  },
  iconBox: {
    width: 44, height: 44, borderRadius: 12,
    alignItems: 'center', justifyContent: 'center',
  },
  cardTitle: { fontSize: 15, fontWeight: '600', color: '#111827' },
  cardSub: { fontSize: 12, color: '#6B7280', marginTop: 2 },
  infoBox: {
    flexDirection: 'row', gap: 8, alignItems: 'flex-start',
    backgroundColor: '#ECFDF5', borderRadius: 12, padding: 14, marginTop: 8,
  },
  infoText: { flex: 1, fontSize: 12, color: '#065F46', lineHeight: 18 },
});