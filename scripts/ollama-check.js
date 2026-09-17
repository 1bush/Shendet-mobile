/**
 * Kontroll i shpejtë i Ollama nga terminali:
 *   npm run ollama          → përdor adresën e paracaktuar
 *   node scripts/ollama-check.js http://192.168.1.50:11434
 *
 * Shfaq: a është aktiv serveri, modelët e instaluar dhe një test bisede.
 */

const baza = (process.argv[2] || process.env.EXPO_PUBLIC_OLLAMA_URL || 'http://localhost:11434')
  .replace(/\/+$/, '');

const modeli = process.env.EXPO_PUBLIC_OLLAMA_MODEL || 'llama3.2';

async function main() {
  console.log(`\n  Kontrolli i Ollama → ${baza}\n`);

  // 1. Serveri aktiv?
  try {
    const res = await fetch(`${baza}/api/tags`, { signal: AbortSignal.timeout(5000) });
    if (!res.ok) throw new Error(`status ${res.status}`);
    const data = await res.json();
    const modelet = (data.models || []).map((m) => m.name);

    console.log(`  [OK] Serveri përgjigjet. Modelë ${modelet.length}.`);
    if (modelet.length === 0) {
      console.log('  [!] Nuk ka modelë. Ekzekuto: ollama pull llama3.2');
    } else {
      modelet.forEach((m) => console.log(`       - ${m}`));
      if (!modelet.some((m) => m.startsWith(modeli))) {
        console.log(`  [!] Modeli "${modeli}" mungon. Ekzekuto: ollama pull ${modeli}`);
      }
    }
  } catch (e) {
    console.log(`  [X] Serveri nuk përgjigjet (${e.message}).`);
    console.log('      Ekzekuto "ollama serve" dhe provo përsëri.');
    process.exitCode = 1;
    return;
  }

  // 2. Test bisede
  try {
    const res = await fetch(`${baza}/api/chat`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      signal: AbortSignal.timeout(60000),
      body: JSON.stringify({
        model: modeli,
        stream: false,
        messages: [{ role: 'user', content: 'Përgjigju vetëm me: OK' }],
      }),
    });
    const data = await res.json();
    const teksti = data?.message?.content?.trim() || '(bosh)';
    console.log(`\n  [OK] Test bisede me "${modeli}": ${teksti.slice(0, 80)}`);
    console.log('  Gati për përdorim në aplikacion.\n');
  } catch (e) {
    console.log(`\n  [X] Testi i bisedës dështoi: ${e.message}\n`);
    process.exitCode = 1;
  }
}

main();