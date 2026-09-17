/**
 * Klient OLLAMA — AI LOKALE për SHENDETI-IM.
 *
 * Pse Ollama:
 *   - Punon 100% lokale (pa pagesë, pa çelësa API, pa të dhëna që dalin jashtë)
 *   - Zëvendëson çdo shërbim cloud (Groq / Anthropic) me një server në rrjetin tënd
 *
 * Zbulimi automatik i adresës (`OLLAMA_BASE_URL`):
 *   1. `EXPO_PUBLIC_OLLAMA_URL` nga `.env` (nëse është vendosur)
 *   2. Android (emulator): http://10.0.2.2:11434   <- 10.0.2.2 = "localhost" i PC-së
 *   3. iOS / web:          http://localhost:11434
 *
 * Në pajisje FIZIKE përdor IP-në e PC-së, p.sh.:
 *   EXPO_PUBLIC_OLLAMA_URL=http://192.168.1.50:11434
 *
 * Modelët (mund të ndryshohen nga .env):
 *   EXPO_PUBLIC_OLLAMA_MODEL=llama3.2          (tekst)
 *   EXPO_PUBLIC_OLLAMA_VISION_MODEL=llava      (foto)
 */

import { Platform } from 'react-native';

/** Adresa e paracaktuar sipas platformës. */
export const OLLAMA_HOST_DEFAULT: string = Platform.select({
  android: 'http://10.0.2.2:11434',
  default: 'http://localhost:11434',
}) as string;

/** Adresa aktive e Ollama (pa "/" në fund). */
export const OLLAMA_BASE_URL: string =
  (process.env.EXPO_PUBLIC_OLLAMA_URL || '').replace(/\/+$/, '') || OLLAMA_HOST_DEFAULT;

/** Modeli për tekst. */
export const OLLAMA_MODEL: string =
  process.env.EXPO_PUBLIC_OLLAMA_MODEL || 'llama3.2';

/** Modeli për analizë fotografie (duhet të mbështesë imazhe, p.sh. llava). */
export const OLLAMA_VISION_MODEL: string =
  process.env.EXPO_PUBLIC_OLLAMA_VISION_MODEL || 'llava';

/** Sa milisekonda pret përgjigjen para se të dështojë. */
const TIMEOUT_MS = 90000;

export type RoliMesazhi = 'system' | 'user' | 'assistant';

export interface MesazhOllama {
  role: RoliMesazhi;
  content: string;
  /** Foto në base64 (pa prefiks "data:image/...") — vetëm për modelet e vizionit. */
  images?: string[];
}

export interface KerkesaOllama {
  messages: MesazhOllama[];
  /** Modeli; nëse mungon përdoret OLLAMA_MODEL. */
  model?: string;
  /** Kërko përgjigje JSON (format: 'json'). */
  json?: boolean;
  /** Vlerë 0–1: sa "krijues" është modeli. */
  temperature?: number;
  timeoutMs?: number;
}

/** Gabim i tipizuar i shtresës Ollama (që ekranet ta kapin lehtë). */
export class OllamaError extends Error {
  constructor(message: string, public readonly iLidhur: boolean = false) {
    super(message);
    this.name = 'OllamaError';
  }
}

/** Kthen true nëse gabimi vjen nga mospasja e lidhjes me serverin. */
export function eshteGabimLidhje(e: unknown): boolean {
  return e instanceof OllamaError && e.iLidhur;
}

/**
 * Thirrja bazë: POST /api/chat me stream=false.
 * Kthen përmbajtjen e tekstit të përgjigjes.
 */
export async function ollamaChat(kerkesa: KerkesaOllama): Promise<string> {
  const { messages, model, json, temperature = 0.2, timeoutMs = TIMEOUT_MS } = kerkesa;

  const kontrollues = new AbortController();
  const timer = setTimeout(() => kontrollues.abort(), timeoutMs);

  try {
    const res = await fetch(`${OLLAMA_BASE_URL}/api/chat`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      signal: kontrollues.signal,
      body: JSON.stringify({
        model: model || OLLAMA_MODEL,
        messages,
        stream: false,
        ...(json ? { format: 'json' } : {}),
        options: { temperature },
      }),
    });

    if (!res.ok) {
      const teksti = await res.text().catch(() => '');
      throw new OllamaError(
        `Ollama u përgjigj me status ${res.status}. ${teksti.slice(0, 200)}`,
        res.status >= 500
      );
    }

    const data = await res.json();
    const permbajtja = data?.message?.content;
    if (typeof permbajtja !== 'string' || permbajtja.trim() === '') {
      throw new OllamaError('Ollama ktheu përgjigje bosh.');
    }
    return permbajtja;
  } catch (e: any) {
    if (e instanceof OllamaError) throw e;
    if (e?.name === 'AbortError') {
      throw new OllamaError('Ollama nuk u përgjigj në kohë (timeout).', true);
    }
    // TypeError: Network request failed — serveri nuk është duke punuar / IP e gabuar
    throw new OllamaError(
      `Nuk u lidh me Ollama (${OLLAMA_BASE_URL}). A është duke punuar "ollama serve"?`,
      true
    );
  } finally {
    clearTimeout(timer);
  }
}

/**
 * Kontrollon nëse serveri Ollama përgjigjet (GET /api/tags).
 * Përdoret për statusin në UI; NUK hedh gabim.
 */
export async function ollamaAktiv(timeoutMs = 4000): Promise<boolean> {
  const kontrollues = new AbortController();
  const timer = setTimeout(() => kontrollues.abort(), timeoutMs);
  try {
    const res = await fetch(`${OLLAMA_BASE_URL}/api/tags`, { signal: kontrollues.signal });
    return res.ok;
  } catch {
    return false;
  } finally {
    clearTimeout(timer);
  }
}

/** Lista e modelëve të instaluar lokalisht. */
export async function ollamaModelet(timeoutMs = 6000): Promise<string[]> {
  const kontrollues = new AbortController();
  const timer = setTimeout(() => kontrollues.abort(), timeoutMs);
  try {
    const res = await fetch(`${OLLAMA_BASE_URL}/api/tags`, { signal: kontrollues.signal });
    if (!res.ok) return [];
    const data = await res.json();
    return (data?.models || [])
      .map((m: any) => String(m?.name || ''))
      .filter((n: string) => n !== '');
  } catch {
    return [];
  } finally {
    clearTimeout(timer);
  }
}

/** Heq gardhin ```json ... ``` që modelet shtojnë shpesh. */
export function pastroJson(tekst: string): string {
  return tekst
    .replace(/^\s*```(?:json)?/i, '')
    .replace(/```\s*$/, '')
    .trim();
}

/**
 * Thirrje me përgjigje JSON të analizuar.
 * Nëse JSON-i është i pavlefshëm, kthen null (thirrësi vendos fallback-un).
 */
export async function ollamaJson<T = unknown>(kerkesa: KerkesaOllama): Promise<T | null> {
  const teksti = await ollamaChat({ ...kerkesa, json: true });
  try {
    return JSON.parse(pastroJson(teksti)) as T;
  } catch {
    return null;
  }
}

export default { ollamaChat, ollamaJson, ollamaAktiv, ollamaModelet, OLLAMA_BASE_URL, OLLAMA_MODEL };