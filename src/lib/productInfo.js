/**
 * Të dhëna lokale për analizën e produkteve në stilin Yuka:
 *  - aditivë me nivele rreziku (bazë e kuruar, shqip)
 *  - përshkrimi i grupeve NOVA (shkalla e përpunimit)
 *  - etiketat e njohura (BIO, vegan, pa gluten...)
 *  - kërkimi i alternativave më të shëndetshme (Open Food Facts v2)
 *
 * Të dhënat e aditivëve janë lokale (pa internet) — vetëm alternativat
 * kërkojnë rrjet. Burimi: rregullorja EU për aditivë + klasifikimi i Yuka-s.
 */

// ─── Aditivë: kodet E me risk ────────────────────────────────────────────────
// risk: "high" | "medium" | "low" | "unknown"
const ADDITIVES = {
  // Bojra artificiale (paralajmërim për hiperaktiviteti në BE)
  e102: { name: "Tartrazine", risk: "high", desc: "Bojë artificiale — lidhet me hiperaktiviteti te fëmijët." },
  e104: { name: "Kinoline e verdhë", risk: "high", desc: "Bojë artificiale — mund të shkaktojë reagime alergjike." },
  e110: { name: "Sunset Yellow", risk: "high", desc: "Bojë artificiale — e ndaluar në disa vende." },
  e122: { name: "Azorubine", risk: "high", desc: "Bojë artificiale — mund të shkaktojë hiperaktiviteti." },
  e124: { name: "Ponceau 4R", risk: "high", desc: "Bojë artificiale — e ndaluar në SHBA." },
  e129: { name: "Allura Red", risk: "high", desc: "Bojë artificiale — paralajmërim për hiperaktiviteti në BE." },
  // Konservantë me risk
  e171: { name: "Dioxid titani", risk: "high", desc: "Bojë nano — e ndaluar në BE (2022) për dyshime gjenotoksike." },
  e211: { name: "Natrium benzoate", risk: "medium", desc: "Konservant — me vitaminë C mund të formojë benzen." },
  e220: { name: "Dioksid squfuri", risk: "medium", desc: "Sulfite — alergji respiratore te astmatikët." },
  e223: { name: "Natrium metabisulfit", risk: "medium", desc: "Sulfite — alergji respiratore." },
  e226: { name: "Kalcium sulfit", risk: "medium", desc: "Sulfite — alergji respiratore." },
  e227: { name: "Kalcium bisulfit", risk: "medium", desc: "Sulfite — alergji respiratore." },
  e249: { name: "Kalium nitrit", risk: "high", desc: "Nitrite — lidhet me nitrosamine kancerogjene." },
  e250: { name: "Natrium nitrit", risk: "high", desc: "Nitrite në mish — lidhet me kancerin e zorrës së trashë." },
  e251: { name: "Natrium nitrat", risk: "high", desc: "Nitrat — shndërrohet në nitrite në trup." },
  e252: { name: "Kalium nitrat", risk: "high", desc: "Nitrat — shndërrohet në nitrite në trup." },
  e320: { name: "BHA", risk: "high", desc: "Antioksidant sintetik — i dyshuar kancerogjen." },
  e321: { name: "BHT", risk: "medium", desc: "Antioksidant sintetik — efekte të diskutueshme hormonale." },
  // Erë-ndjellës / ëmbëltues
  e621: { name: "Glutamat mononatrik (MSG)", risk: "medium", desc: "Erë-ndjellës — reagime te të ndjeshmit." },
  e627: { name: "Acid guanilic", risk: "medium", desc: "Erë-ndjellës — shpesh përdoret me MSG." },
  e631: { name: "Acid inosinic", risk: "medium", desc: "Erë-ndjellës — shpesh përdoret me MSG." },
  e635: { name: "Disodium ribonukleotid", risk: "medium", desc: "Erë-ndjellës — shpesh përdoret me MSG." },
  e951: { name: "Aspartam", risk: "medium", desc: "Ëmbëltues — kujdes me fenilketonuri (PKU)." },
  e952: { name: "Ciklamati", risk: "medium", desc: "Ëmbëltues — të ndaluar në disa vende." },
  e954: { name: "Sakarina", risk: "medium", desc: "Ëmbëltues — shije metalike, debate shkencore." },
  e407: { name: "Karraginan", risk: "medium", desc: "Trashues — diskutime për inflamacionin e zorrës." },
};


// ─── Aditivë me risk të ulët (të sigurt) ─────────────────────────────────────
const ADDITIVES_SAFE = {
  e100: { name: "Kurkuminë", risk: "low", desc: "Bojë natyrore nga shafrani." },
  e120: { name: "Karmine", risk: "low", desc: "Bojë natyrore nga insektet — jo vegan." },
  e150a: { name: "Karamel i thjeshtë", risk: "low", desc: "Karamel klasik pa sulfite." },
  e160a: { name: "Karotenoide", risk: "low", desc: "Pigment natyror (pro-vitamin A)." },
  e200: { name: "Acid sorbik", risk: "low", desc: "Konservant me profil të sigurt." },
  e202: { name: "Sorbate kaliumi", risk: "low", desc: "Konservant me profil të sigurt." },
  e300: { name: "Acid askorbik (Vit. C)", risk: "low", desc: "Antioksidant — vitaminë." },
  e301: { name: "Askorbat natriku", risk: "low", desc: "Derivat i vitaminës C." },
  e306: { name: "Tokoferol (Vit. E)", risk: "low", desc: "Antioksidant natyror — vitaminë." },
  e322: { name: "Lecitinë", risk: "low", desc: "Emulsifier natyror (mbjellës ose soje)." },
  e330: { name: "Acid citrik", risk: "low", desc: "Aciditet natyror — i sigurt." },
  e331: { name: "Citrate natriku", risk: "low", desc: "Rregullues aciditeti — i sigurt." },
  e410: { name: "Gumë akacie", risk: "low", desc: "Fibër natyral — i dobishëm për tretjen." },
  e412: { name: "Gumë guar", risk: "low", desc: "Fibër natyral — i dobishëm për tretjen." },
  e415: { name: "Gumë ksantane", risk: "low", desc: "Trashues bakterial — i sigurt." },
  e466: { name: "Karboksimilcelulozë", risk: "low", desc: "Trashues — i sigurt në sasi ushqimore." },
  e500: { name: "Karbonat natriku", risk: "low", desc: "Ngritës buke — i sigurt." },
  e501: { name: "Karbonat kaliumi", risk: "low", desc: "Ngritës — i sigurt." },
  e503: { name: "Karbonat amoniaku", risk: "low", desc: "Ngritës — i sigurt." },
  e509: { name: "Klorur kaliumi", risk: "low", desc: "Përdoret si zëvendësues i kripës." },
  e961: { name: "Neotame", risk: "low", desc: "Ëmbëltues — i miratuar nga BE." },
};

Object.assign(ADDITIVES, ADDITIVES_SAFE);

/** Përkthimi i niveleve të rrezikut (ngjyra + etiketa shqip). */
export const RISK = {
  high: { label: "Risk i lartë", color: "#e74c3c" },
  medium: { label: "Risk i moderuar", color: "#e67e22" },
  low: { label: "Risk i ulët", color: "#27ae60" },
  unknown: { label: "I panjohur", color: "#95a5a6" },
};

// Emra alternativë që shfaqen te additives_tags (pa prefiksin "en:")
const ALIASES = {
  msg: "e621",
  aspartame: "e951",
  "sodium-nitrite": "e250",
  sodiumnitrite: "e250",
  "sodium-nitrate": "e251",
  "sodium-benzoate": "e211",
  benzoicacid: "e211",
  "sulfur-dioxide": "e220",
  sulfurdioxide: "e220",
  "titanium-dioxide": "e171",
  titaniumdioxide: "e171",
  saccharin: "e954",
};

/**
 * Informacioni për një aditiv nga etiketa e OFF ("en:e621", "en:msg"...).
 * @returns {{code: string, name: string|null, risk: string, desc: string}}
 */
export function getAdditiveInfo(tag) {
  const raw = String(tag || "").replace(/^en:/, "").trim().toLowerCase();
  const key = ALIASES[raw] || raw;
  const hit = ADDITIVES[key];
  if (hit) return { code: key.toUpperCase(), ...hit };
  const isEcode = /^e\d{1,3}[a-z]?$/.test(key);
  return {
    code: isEcode ? key.toUpperCase() : raw.toUpperCase(),
    name: isEcode ? null : raw,
    risk: "unknown",
    desc: "Të dhëna të pamjaftueshme — rekomandohet kujdes.",
  };
}

/**
 * Penaltitet e pikëve për aditivët (si Yuka: maksimumi -21).
 * @param {string[]} tags additives_tags nga Open Food Facts
 */
export function additivePenalty(tags) {
  let pen = 0;
  (tags || []).forEach((t) => {
    const r = getAdditiveInfo(t).risk;
    if (r === "high") pen += 6;
    else if (r === "medium") pen += 3;
  });
  return Math.min(pen, 21);
}

// ─── Grupi NOVA: shkalla e përpunimit ────────────────────────────────────────
const NOVA = {
  1: {
    title: "Ushqime të papërpunuara",
    desc: "Gjithçka të plota pa përpunim: fruta, perime, qumësht, vezë, mish i papërpunuar.",
    color: "#27ae60",
  },
  2: {
    title: "Përbërës kuzhine",
    desc: "Përpunim i lehtë shtëpiak: vaj, kripë, sheqer, gjalpë, miell.",
    color: "#8bc34a",
  },
  3: {
    title: "Ushqime të përpunuara",
    desc: "Ushqime të përpunuara nga industria: konserva, bukë e pjekur, djathë i vendosur.",
    color: "#ff9800",
  },
  4: {
    title: "Ultra-të përpunuara",
    desc: "Formula industriale me shumë aditivë, sheqer dhe yndyra të shtuara — shmangni sa më shumë.",
    color: "#e74c3c",
  },
};

/**
 * Info për grupin NOVA (1–4).
 * @returns {{title: string, desc: string, color: string}|null}
 */
export function getNovaInfo(group) {
  const g = Number(group);
  return NOVA[g] || null;
}

// ─── Etiketat e njohura → shqip ──────────────────────────────────────────────
const LABEL_DEFS = [
  { match: ["organic", "eu-organic", "bio"], text: "🌱 BIO / Organik", color: "#27ae60" },
  { match: ["vegan"], text: "🌱 Vegan", color: "#27ae60" },
  { match: ["vegetarian"], text: "🥬 Vegjetarian", color: "#8bc34a" },
  { match: ["gluten-free", "no-gluten"], text: "🌾 Pa gluten", color: "#e67e22" },
  { match: ["lactose-free", "no-lactose"], text: "🥛 Pa laktozë", color: "#e67e22" },
  { match: ["fair-trade", "fairtrade"], text: "🤝 Tregtim i drejtë", color: "#1a7a4a" },
  { match: ["halal"], text: "☪ Halal", color: "#1a7a4a" },
  { match: ["kosher"], text: "✡ Kosher", color: "#1a7a4a" },
];

/**
 * Etiketat e njohura të produktit, të gatshme për shfaqje.
 * @param {object} p produkti nga Open Food Facts
 * @returns {Array<{text: string, color: string}>}
 */
export function getLabels(p) {
  const tags = (p.labels_tags || []).join(" ").toLowerCase();
  return LABEL_DEFS.filter((d) => d.match.some((m) => tags.includes(m)));
}

/** A ka produkti etiketë BIO/organike? */
export function hasOrganicLabel(p) {
  return getLabels(p).some((l) => l.text.includes("BIO"));
}

// ─── Alternativat më të shëndetshme (Open Food Facts v2) ─────────────────────
const GRADE_RANK = { a: 0, b: 1, c: 2, d: 3, e: 4 };
// Kategori shumë të gjëra që jepen rezultate të parëndësishme
const GENERIC_TAGS = new Set(["en:foods", "en:products"]);
// Cache brenda sesionit: kategori → lista e filtruar (shmang rate-limit e OFF)
const altCache = new Map();

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

/** Kërkim me retry + backoff (OFF kufizon përdoruesit anonimë me 503). */
async function searchByTag(tag) {
  const url =
    `https://world.openfoodfacts.org/api/v2/search?categories_tags=${encodeURIComponent(tag)}` +
    "&fields=code,product_name,brands,nutriscore_grade,image_front_small_url" +
    "&page_size=30&sort_by=popularity_key";
  let lastErr = null;
  for (let i = 0; i < 2; i++) {
    try {
      const res = await fetch(url, { headers: { Accept: "application/json" } });
      if (res.status === 200) return await res.json();
      lastErr = new Error(`HTTP ${res.status}`);
    } catch (e) {
      lastErr = e;
    }
    await sleep(1500);
  }
  throw lastErr;
}

/**
 * Gjen produkte më të mirë (NutriScore më i ulët se i këtij) nga e njëjta
 * kategori, të renditura sipas popullaritetit.
 *
 * Kthen deri në 6 alternative; [] nëse produkti është tashmë grade A ose
 * nëse rrjeti/rate-limit dështon (kartë e fshehur — nuk prish ekranin).
 *
 * @param {object} p produkti nga Open Food Facts
 * @returns {Promise<Array<{code, product_name, brands, nutriscore_grade, image_front_small_url}>>}
 */
export async function fetchAlternatives(p) {
  const own = String(p.code || "");
  const myGrade = (p.nutriscore_grade || "").toLowerCase();
  if (myGrade === "a") return []; // grade A nuk ka alternativë më të mirë
  const betterThan = GRADE_RANK[myGrade] != null ? GRADE_RANK[myGrade] : 3;

  const tags = [];
  [...(p.categories_hierarchy || []), ...(p.categories_tags || [])].forEach((t) => {
    if (!GENERIC_TAGS.has(t) && !tags.includes(t)) tags.push(t);
  });

  const pick = (products) =>
    (products || [])
      .filter((x) => {
        if (!x.code || x.code === own || !x.product_name) return false;
        const g = (x.nutriscore_grade || "").toLowerCase();
        return GRADE_RANK[g] != null && GRADE_RANK[g] < betterThan;
      })
      .slice(0, 6);

  // Nis nga kategoria më specifike (alternativa më e ngjashme) → më e gjerë
  const ordered = [...tags].reverse().slice(0, 4);

  for (const tag of ordered) {
    if (altCache.has(tag)) {
      const cached = altCache.get(tag);
      if (cached.length >= 2) return cached;
      continue;
    }
    try {
      const data = await searchByTag(tag);
      const list = pick(data.products);
      altCache.set(tag, list);
      if (list.length >= 2) return list;
    } catch {
      // vazhdo me tagun tjetër
    }
  }
  // Kthe listën më të mirë nga cache (nëse ndonjë tag dha diçka)
  for (const tag of ordered) {
    const cached = altCache.get(tag);
    if (cached && cached.length) return cached;
  }
  return [];
}


