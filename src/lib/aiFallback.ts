/**
 * Fallback LOKAL (pa AI) për ekranet që përdorin Ollama.
 *
 * Pse ekziston: nëse Ollama nuk shtë duke punuar (serveri i fikur, IP e gabuar,
 * modeli nuk është tërhequr), aplikacioni NUK duhet të ngecë — kthen përgjigje
 * bazë lokale ose një mesazh të qartë shqip.
 *
 * Këto të dhëna janë EDUKATIVE, jo mjekësore.
 */

/** Mesazhi standard kur serveri Ollama nuk përgjigjet. */
export const MSG_PA_OLLAMA =
  'AI lokale (Ollama) nuk është duke punuar. Përgjigja vjen nga të dhënat lokale.\n\n' +
  'Për AI të plotë: hap terminalin dhe ekzekuto "ollama serve", pastaj "ollama pull llama3.2".';

/* ─────────────────────────── ASISTENTI AI ─────────────────────────── */

/** Përgjigje bazë në shqip sipas fjalëve kyçe (funksionon 100% offline). */
export function pergjigjeLokale(pyetja: string): string {
  const p = (pyetja || '').toLowerCase();

  if (p.includes('nutriscore') || p.includes('nutri-score')) {
    return (
      'NutriScore shtë një sistem vlerësimi nga A (më i miri) deri E (më i keqi), ' +
      'bazuar në sheqerin, yndyrën e ngopur, kripën, fibrat, proteinat dhe përbërësit.\n\n' +
      'A/B = zgjedhje e mirë · C = e pranueshme · D/E = konsumo me kujdes.'
    );
  }
  if (p.includes('sheqer')) {
    return (
      'Sheqeri nuk është "helm", por teprica e tij dëmton. OBSH rekomandon ' +
      'më pak se 25 g sheqer të shtuar në ditë për të rriturit.\n\n' +
      'Këshillë: lexo "sheqer" tek vlerat ushqyese për 100 g — mbi 15 g/100 g është shumë.'
    );
  }
  if (p.includes('energji') || p.includes('lodh')) {
    return (
      'Për energji të qëndrueshme zgjidh: drithëra të plota, fasule/thjerrme, ' +
      'fruta të thata, arra, banane dhe ujë të mjaftueshëm.\n\n' +
      'Shmang pijet energjike — japin rritje të shpejtë dhe rënie të menjëhershme.'
    );
  }
  if (p.includes('etiket') || p.includes('lexo')) {
    return (
      'Si të lexosh etiketat:\n' +
      '1. Shiko listën e përbërësve — renditja është nga më i shumti tek më i pakti\n' +
      '2. Krahaso vlerat për 100 g (jo për porcion)\n' +
      '3. Kontrollo sheqerin, yndyrën e ngopur dhe kripën\n' +
      '4. Sa më e shkurtër lista e përbërësve, zakonisht aq më mirë'
    );
  }
  if (p.includes('ilaç') || p.includes('barn') || p.includes('doz')) {
    return (
      'Për ilaçet: nuk jap diagnoza apo doza. Dozimi caktohet vetëm nga mjeku ose farmacisti.\n\n' +
      'Për informacion bazë, hap modulin "Barnat & Ilaçet". Për çdo problem shëndetësor ' +
      'serioz, konsulto mjekun.'
    );
  }
  if (p.includes('alergji') || p.includes('gluten') || p.includes('laktoz')) {
    return (
      'Për alergjitë: kontrollo gjithmonë seksionin "Alergjenët" dhe listën e përbërësve ' +
      'të produktit. Fjalët kyçe shfaqen shpesh në MAJUSKULA (p.sh. QUMËSHT, ARRA).\n\n' +
      'Në rast alergjie të rëndë, lexo etiketën çdo herë — recetat ndryshojnë.'
    );
  }
  if (p.includes('diet') || p.includes('humb') || p.includes('pesh')) {
    return (
      'Për një dietë të ekuilibruar: gjysmë pjata perime/fruta, një e katërta proteina, ' +
      'një e katërta drithëra të plota, plus ujë.\n\n' +
      'Ndryshimet e vogla por të vazhdueshme funksionojnë më mirë se dietat e rrepta.'
    );
  }

  return (
    MSG_PA_OLLAMA +
    '\n\nNdërkohë, mund të më pyesësh për: NutriScore, sheqerin, energjinë, ' +
    'leximin e etiketave, alergjitë dhe dietat.'
  );
}

/* ────────────────────────── BARNAT & ILAÇET ─────────────────────────── */

/** Bazë e vogël lokale (edukative) për shembujt e zakonshëm. */
const BARNAT_LOKALE: Record<string, any> = {
  paracetamol: {
    emri: 'Paracetamol',
    lloji: 'Analgjezik / antipiretik',
    perdorimi: 'Përdoret për dhimbje të lehta deri mesatare dhe për uljen e temperaturës.',
    dozimi: 'Të rritur: 500–1000 mg çdo 4–6 orë, maksimumi 3–4 g në 24 orë.',
    efekte_anesore: ['Të përziera', 'Djersitje', 'Rritje e enzimave të mëlçisë (doza të larta)'],
    kujdes: [
      'Mos e kombino me produkte të tjera që përmbajnë paracetamol',
      'Kujdes në sëmundje të mëlçisë',
      'Alkooli rrit rrezikun',
    ],
    nderveprime: 'Warfarina mund të rrisë efektin; alkooli rrit rrezikun për mëlçinë.',
    ekuivalente: 'Panadol, Efferalgan, Febric',
  },
  ibuprofen: {
    emri: 'Ibuprofen',
    lloji: 'Anti-inflamator jo-steroid (NSAID)',
    perdorimi: 'Përdoret për dhimbje, inflamacion dhe temperaturë.',
    dozimi: 'Të rritur: 200–400 mg çdo 6–8 orë, maksimumi 1200 mg në ditë pa recetë.',
    efekte_anesore: ['Dhimbje stomaku', 'Të përziera', 'Marramendje', 'Rritje e presionit'],
    kujdes: [
      'Merr pas ngrënies',
      'Shmang në ulçerë ose gastrit',
      'Kujdes në sëmundje të veshkave',
    ],
    nderveprime:
      'Rrit rrezikun e gjakderdhjes me antikoagulantë; bashkë me kortikosteroide rrit rrezikun ulcerogjen.',
    ekuivalente: 'Brufen, Nurofen, Advil',
  },
  amoxicilin: {
    emri: 'Amoxicilinë',
    lloji: 'Antibiotik (penicilinë)',
    perdorimi: 'Përdoret për infeksione bakteriale (vesh, fyt, rrugë urinare, lëkurë).',
    dozimi: 'Të rritur: 500 mg çdo 8 orë ose 875 mg çdo 12 orë, sipas recetës.',
    efekte_anesore: ['Diarre', 'Të përziera', 'Skuqje lëkure', 'Kandidiazë'],
    kujdes: [
      'VETËM me recetë',
      'Përfundo kursin e plotë',
      'Kujdes në alergji ndaj penicilinës',
    ],
    nderveprime:
      'Metotreksati rrit toksicitetin; allopurinoli rrit rrezikun e skuqjes.',
    ekuivalente: 'Amoxil, Hiconcil, Clamoxyl',
  },
  omeprazol: {
    emri: 'Omeprazol',
    lloji: 'Inhibitor i pompës protonike (IPP)',
    perdorimi: 'Ul aciditetin e stomakut: refluks, gastrit, ulçerë.',
    dozimi: 'Të rritur: 20 mg një herë në ditë, 30 minuta para mëngjesit.',
    efekte_anesore: ['Dhimbje koke', 'Diarre ose kapsllëk', 'Fryrje'],
    kujdes: [
      'Përdorim afatshkurtër pa mbikëqyrje',
      'Mund të maskojë sëmundje serioze',
      'Mund të ulë vitaminën B12',
    ],
    nderveprime:
      'Ulet thithja e klopidogrelit; ndryshon nivelet e disa ilaçeve të tjera.',
    ekuivalente: 'Losec, Omez, Ultop',
  },
  metformin: {
    emri: 'Metformin',
    lloji: 'Antidiabetik (biguanid)',
    perdorimi: 'Përdoret në diabetin e tipit 2 për uljen e sheqerit në gjak.',
    dozimi: 'Fillestare 500 mg 1–2 herë në ditë, rritet gradualisht sipas mjekut.',
    efekte_anesore: ['Diarre', 'Të përziera', 'Metalizëm në gojë', 'Ulje e vitaminës B12'],
    kujdes: [
      'Merr me ushqim',
      'Ndalo para ekzaminimeve me kontrast',
      'Kujdes në funksion të veshkave',
    ],
    nderveprime:
      'Alkooli rrit rrezikun e acidit laktik; diuretikët ndryshojnë efektin.',
    ekuivalente: 'Glucophage, Siofor, Metfogamma',
  },
};

/**
 * Kërkim lokal i barnës.
 * @returns objekt MedInfo ose null nëse nuk gjendet lokalisht.
 */
export function barnaLokale(emri: string): any | null {
  const k = (emri || '').toLowerCase().trim();
  if (!k) return null;
  const celesi = Object.keys(BARNAT_LOKALE).find(
    (key) => k.includes(key) || key.includes(k)
  );
  return celesi ? BARNAT_LOKALE[celesi] : null;
}

/* ─────────────────────────── SKANIMI I FATURËS ─────────────────────────── */

/** Përgjigje bazë kur nuk ka AI: pa produkte + shpjegim. */
export function faturaLokale(): any {
  return {
    produktet: [],
    skor_total: 0,
    permbledhje: 'Analiza AI nuk u krye (Ollama nuk është aktiv).',
    keshilla:
      'Për të analizuar faturën me AI lokale: ekzekuto "ollama serve" dhe ' +
      '"ollama pull llava" në kompjuter, pastaj provo përsëri.',
  };
}

export default { pergjigjeLokale, barnaLokale, faturaLokale, MSG_PA_OLLAMA };