import React, { useState } from 'react';
import {
  View, Text, TouchableOpacity, ScrollView, StyleSheet,
  SafeAreaView, TextInput, Modal, Switch,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import AsyncStorage from '@react-native-async-storage/async-storage';

export type Profili = {
  id: string;
  emri: string;
  mosha: string;
  ikona: string;
  alergji: string[];
  dieta: string[];
  aktiv: boolean;
};

const IKONAT = ['👤','👶','🧒','👦','👧','🧑','👨','👩','🧓','👴','👵'];
const ALERGJI_LISTA = ['Gluten', 'Laktozë', 'Arra', 'Kikirikë', 'Vezë', 'Peshk', 'Selino', 'Soje'];
const DIETA_LISTA = ['Vegjetarian', 'Vegan', 'Keto', 'Diabetik', 'Hipertension', 'Celiak'];

const DEFAULT_PROFILES: Profili[] = [
  { id: '1', emri: 'Unë', mosha: 'I rritur', ikona: '👤', alergji: [], dieta: [], aktiv: true },
];

export function useProfilet() {
  const [profilet, setProfilet] = useState<Profili[]>(DEFAULT_PROFILES);
  const aktiv = profilet.find(p => p.aktiv) || profilet[0];

  const aktivizoProfilin = (id: string) =>
    setProfilet(prev => prev.map(p => ({ ...p, aktiv: p.id === id })));

  const shtoProfilin = (p: Profili) => setProfilet(prev => [...prev, p]);
  const fshiProfilin = (id: string) =>
    setProfilet(prev => {
      const filtered = prev.filter(p => p.id !== id);
      if (filtered.length > 0 && !filtered.find(p => p.aktiv)) filtered[0].aktiv = true;
      return filtered;
    });

  return { profilet, aktiv, aktivizoProfilin, shtoProfilin, fshiProfilin };
}

export default function ProfiletFamiljare() {
  const { profilet, aktiv, aktivizoProfilin, shtoProfilin, fshiProfilin } = useProfilet();
  const [modalVisible, setModalVisible] = useState(false);
  const [emri, setEmri] = useState('');
  const [mosha, setMosha] = useState('I rritur');
  const [ikonaZgjedhur, setIkonaZgjedhur] = useState('👤');
  const [alergjiZgjedhura, setAlergjiZgjedhura] = useState<string[]>([]);
  const [dietaZgjedhura, setDietaZgjedhura] = useState<string[]>([]);

  const toggleAllergen = (a: string) =>
    setAlergjiZgjedhura(prev => prev.includes(a) ? prev.filter(x => x !== a) : [...prev, a]);
  const toggleDiet = (d: string) =>
    setDietaZgjedhura(prev => prev.includes(d) ? prev.filter(x => x !== d) : [...prev, d]);

  const ruajProfilin = () => {
    if (!emri.trim()) return;
    const i: Profili = {
      id: Date.now().toString(),
      emri: emri.trim(), mosha, ikona: ikonaZgjedhur,
      alergji: alergjiZgjedhura, dieta: dietaZgjedhura, aktiv: false,
    };
    shtoProfilin(i);
    setModalVisible(false);
    setEmri(''); setMosha('I rritur'); setIkonaZgjedhur('👤');
    setAlergjiZgjedhura([]); setDietaZgjedhura([]);
  };

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Profili Familjar</Text>
        <TouchableOpacity style={styles.addBtn} onPress={() => setModalVisible(true)}>
          <Ionicons name="add" size={22} color="white" />
        </TouchableOpacity>
      </View>

      <ScrollView contentContainerStyle={{ padding: 16, paddingBottom: 32 }}>
        {/* Active profile */}
        {aktiv && (
          <View style={styles.activeCard}>
            <Text style={styles.activeIcon}>{aktiv.ikona}</Text>
            <View style={{ flex: 1 }}>
              <Text style={styles.activeName}>{aktiv.emri}</Text>
              <Text style={styles.activeMosha}>{aktiv.mosha} · Profili aktiv</Text>
              {aktiv.alergji.length > 0 && (
                <Text style={styles.activeMeta}>⚠️ Alergji: {aktiv.alergji.join(', ')}</Text>
              )}
              {aktiv.dieta.length > 0 && (
                <Text style={styles.activeMeta}>🥗 Dietë: {aktiv.dieta.join(', ')}</Text>
              )}
            </View>
          </View>
        )}

        <Text style={styles.sectionLabel}>Të gjithë profilet</Text>

        {profilet.map(p => (
          <TouchableOpacity key={p.id} style={[styles.profileCard, p.aktiv && styles.profileCardActive]}
            onPress={() => aktivizoProfilin(p.id)} activeOpacity={0.8}>
            <Text style={styles.profileIcon}>{p.ikona}</Text>
            <View style={{ flex: 1 }}>
              <Text style={styles.profileName}>{p.emri}</Text>
              <Text style={styles.profileSub}>{p.mosha}</Text>
              {p.alergji.length > 0 && (
                <Text style={styles.profileMeta} numberOfLines={1}>⚠️ {p.alergji.join(', ')}</Text>
              )}
            </View>
            {p.aktiv
              ? <Ionicons name="checkmark-circle" size={22} color="#1D9E75" />
              : profilet.length > 1
                ? <TouchableOpacity onPress={() => fshiProfilin(p.id)}>
                    <Ionicons name="trash-outline" size={18} color="#EF4444" />
                  </TouchableOpacity>
                : null}
          </TouchableOpacity>
        ))}

        <Text style={styles.hint}>
          Profili aktiv ndikon skanimin — alergjinitë do të sinjalizohen automatikisht.
        </Text>
      </ScrollView>

      {/* Add Profile Modal */}
      <Modal visible={modalVisible} animationType="slide" transparent>
        <View style={styles.modalBg}>
          <View style={styles.modalCard}>
            <View style={styles.modalHeader}>
              <Text style={styles.modalTitle}>Profil i ri</Text>
              <TouchableOpacity onPress={() => setModalVisible(false)}>
                <Ionicons name="close" size={24} color="#6B7280" />
              </TouchableOpacity>
            </View>

            <ScrollView showsVerticalScrollIndicator={false}>
              <Text style={styles.fieldLabel}>Emri</Text>
              <TextInput style={styles.input} value={emri} onChangeText={setEmri}
                placeholder="p.sh. Babi, Nëna, Fëmija..." placeholderTextColor="#9CA3AF" />

              <Text style={styles.fieldLabel}>Grupmoshe</Text>
              <View style={styles.moshaBtnRow}>
                {['Fëmijë', 'Adoleshent', 'I rritur', 'Moshatar'].map(m => (
                  <TouchableOpacity key={m} style={[styles.moshaBtn, mosha === m && styles.moshaBtnActive]}
                    onPress={() => setMosha(m)}>
                    <Text style={[styles.moshaBtnText, mosha === m && styles.moshaBtnTextActive]}>{m}</Text>
                  </TouchableOpacity>
                ))}
              </View>

              <Text style={styles.fieldLabel}>Ikonë</Text>
              <View style={styles.ikonatRow}>
                {IKONAT.map(ik => (
                  <TouchableOpacity key={ik} style={[styles.ikonaBtn, ikonaZgjedhur === ik && styles.ikonaBtnActive]}
                    onPress={() => setIkonaZgjedhur(ik)}>
                    <Text style={{ fontSize: 24 }}>{ik}</Text>
                  </TouchableOpacity>
                ))}
              </View>

              <Text style={styles.fieldLabel}>Alergji</Text>
              <View style={styles.chipsRow}>
                {ALERGJI_LISTA.map(a => (
                  <TouchableOpacity key={a}
                    style={[styles.chip, alergjiZgjedhura.includes(a) && styles.chipActive]}
                    onPress={() => toggleAllergen(a)}>
                    <Text style={[styles.chipText, alergjiZgjedhura.includes(a) && styles.chipTextActive]}>{a}</Text>
                  </TouchableOpacity>
                ))}
              </View>

              <Text style={styles.fieldLabel}>Dietë / Kushte shëndetësore</Text>
              <View style={styles.chipsRow}>
                {DIETA_LISTA.map(d => (
                  <TouchableOpacity key={d}
                    style={[styles.chip, dietaZgjedhura.includes(d) && styles.chipActiveGreen]}
                    onPress={() => toggleDiet(d)}>
                    <Text style={[styles.chipText, dietaZgjedhura.includes(d) && styles.chipTextActive]}>{d}</Text>
                  </TouchableOpacity>
                ))}
              </View>

              <TouchableOpacity style={[styles.saveBtn, !emri.trim() && { opacity: 0.5 }]}
                onPress={ruajProfilin} disabled={!emri.trim()}>
                <Text style={styles.saveBtnText}>Ruaj Profilin</Text>
              </TouchableOpacity>
            </ScrollView>
          </View>
        </View>
      </Modal>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#F9FAFB' },
  header: { flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between',
    padding: 20, backgroundColor: 'white', borderBottomWidth: 1, borderBottomColor: '#E5E7EB' },
  title: { fontSize: 22, fontWeight: '700', color: '#111827' },
  addBtn: { width: 36, height: 36, borderRadius: 10, backgroundColor: '#1D9E75',
    alignItems: 'center', justifyContent: 'center' },
  activeCard: { flexDirection: 'row', alignItems: 'center', gap: 12,
    backgroundColor: '#ECFDF5', borderRadius: 16, padding: 16, marginBottom: 16,
    borderWidth: 1.5, borderColor: '#1D9E75' },
  activeIcon: { fontSize: 40 },
  activeName: { fontSize: 18, fontWeight: '700', color: '#111827' },
  activeMosha: { fontSize: 12, color: '#1D9E75', marginTop: 1 },
  activeMeta: { fontSize: 12, color: '#374151', marginTop: 3 },
  sectionLabel: { fontSize: 12, fontWeight: '600', color: '#6B7280',
    textTransform: 'uppercase', letterSpacing: 0.5, marginBottom: 10 },
  profileCard: { flexDirection: 'row', alignItems: 'center', gap: 12,
    backgroundColor: 'white', borderRadius: 12, padding: 14, marginBottom: 8,
    borderWidth: 1, borderColor: '#E5E7EB' },
  profileCardActive: { borderColor: '#1D9E75', borderWidth: 1.5 },
  profileIcon: { fontSize: 32 },
  profileName: { fontSize: 15, fontWeight: '600', color: '#111827' },
  profileSub: { fontSize: 12, color: '#6B7280' },
  profileMeta: { fontSize: 11, color: '#9CA3AF', marginTop: 2 },
  hint: { fontSize: 12, color: '#9CA3AF', textAlign: 'center', marginTop: 16, lineHeight: 18 },
  modalBg: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'flex-end' },
  modalCard: { backgroundColor: 'white', borderTopLeftRadius: 24, borderTopRightRadius: 24,
    padding: 20, maxHeight: '90%' },
  modalHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 },
  modalTitle: { fontSize: 20, fontWeight: '700', color: '#111827' },
  fieldLabel: { fontSize: 13, fontWeight: '600', color: '#374151', marginBottom: 8, marginTop: 12 },
  input: { backgroundColor: '#F3F4F6', borderRadius: 10, paddingHorizontal: 14,
    paddingVertical: 12, fontSize: 15, color: '#111827' },
  moshaBtnRow: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  moshaBtn: { borderRadius: 20, paddingHorizontal: 14, paddingVertical: 7,
    backgroundColor: '#F3F4F6', borderWidth: 1, borderColor: '#E5E7EB' },
  moshaBtnActive: { backgroundColor: '#1D9E75', borderColor: '#1D9E75' },
  moshaBtnText: { fontSize: 13, color: '#374151' },
  moshaBtnTextActive: { color: 'white', fontWeight: '600' },
  ikonatRow: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  ikonaBtn: { width: 48, height: 48, borderRadius: 12, alignItems: 'center', justifyContent: 'center',
    backgroundColor: '#F3F4F6', borderWidth: 1, borderColor: '#E5E7EB' },
  ikonaBtnActive: { borderColor: '#1D9E75', borderWidth: 2, backgroundColor: '#ECFDF5' },
  chipsRow: { flexDirection: 'row', flexWrap: 'wrap', gap: 8 },
  chip: { borderRadius: 20, paddingHorizontal: 12, paddingVertical: 6,
    backgroundColor: '#F3F4F6', borderWidth: 1, borderColor: '#E5E7EB' },
  chipActive: { backgroundColor: '#FEF3C7', borderColor: '#F59E0B' },
  chipActiveGreen: { backgroundColor: '#ECFDF5', borderColor: '#1D9E75' },
  chipText: { fontSize: 13, color: '#374151' },
  chipTextActive: { fontWeight: '600', color: '#374151' },
  saveBtn: { backgroundColor: '#1D9E75', borderRadius: 12, padding: 14,
    alignItems: 'center', marginTop: 20, marginBottom: 8 },
  saveBtnText: { color: 'white', fontWeight: '700', fontSize: 16 },
});
