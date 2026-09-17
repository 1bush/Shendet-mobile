/**
 * Ruajtja LOKALE e historisë së kërkimeve (AsyncStorage).
 *
 * Përdoret nga FoodScreen / AnimalFoodScreen / CosmeticsScreen përmes
 * `HistoryPanel`, kështu që ekranet tregojnë historikun edhe pa internet.
 */

import AsyncStorage from "@react-native-async-storage/async-storage";

/** Sa zëra mbahen për çdo lloj (më të rejat në krye). */
const MAX_ZERA = 50;

/** Kufizuesit e emrit të çelësit: historia:<lloj> */
const celesi = (lloj) => `historia:${lloj}`;

/**
 * Shton një zë në histori.
 * @param {string} lloj  "food" | "animal" | "cosmetics" | ...
 * @param {object} zeri  { name, score, barcode, nutrients? }
 */
export async function addToHistory(loj, zeri) {
  try {
    const zera = await getHistory(loj);
    const i_ri = {
      id: `${Date.now()}`,
      date: new Date().toISOString(),
      ...zeri,
    };
    // Heq dublikatët e të njëjtit barkod dhe e vendos të renë në krye.
    const paDublikat = zera.filter(
      (z) => !(zeri?.barcode && z.barcode === zeri.barcode)
    );
    const lista = [i_ri, ...paDublikat].slice(0, MAX_ZERA);
    await AsyncStorage.setItem(celesi(loj), JSON.stringify(lista));
    return i_ri;
  } catch (e) {
    // Historia nuk duhet të prishë kërkimin — dështimi injorohet me log.
    console.warn("addToHistory dështoi:", e);
    return null;
  }
}

/**
 * Lexon historinë e një lloji (më e reja e para).
 * @returns {Promise<Array>}
 */
export async function getHistory(loj) {
  try {
    const raw = await AsyncStorage.getItem(celesi(loj));
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [];
  } catch (e) {
    console.warn("getHistory dështoi:", e);
    return [];
  }
}

/** Fshin historinë e një lloji. */
export async function clearHistory(loj) {
  try {
    await AsyncStorage.removeItem(celesi(loj));
    return true;
  } catch (e) {
    console.warn("clearHistory dështoi:", e);
    return false;
  }
}

/** Fshin të gjithë historinë e aplikacionit. */
export async function clearAllHistory(lojet = ["food", "animal", "cosmetics"]) {
  try {
    await AsyncStorage.multiRemove(lojet.map(celesi));
    return true;
  } catch (e) {
    console.warn("clearAllHistory dështoi:", e);
    return false;
  }
}

/** Formaton datën në shqip të shkurtër (p.sh. "17/09, 22:41"). */
export function formatoDate(iso) {
  try {
    const d = new Date(iso);
    const dita = String(d.getDate()).padStart(2, "0");
    const muaji = String(d.getMonth() + 1).padStart(2, "0");
    const ora = String(d.getHours()).padStart(2, "0");
    const minuta = String(d.getMinutes()).padStart(2, "0");
    return `${dita}/${muaji}, ${ora}:${minuta}`;
  } catch {
    return "";
  }
}