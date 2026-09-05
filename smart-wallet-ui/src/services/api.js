const API_URL = 'http://localhost:8080/api/v1';

async function parseError(res) {
    try {
        const body = await res.json();
        return body.detail || body.title || 'İşlem başarısız oldu.';
    } catch {
        return 'Beklenmeyen bir hata oluştu.';
    }
}

export const registerUser = async ({ email, password, iban }) => {
    const res = await fetch(`${API_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, iban: iban || null }),
    });
    if (!res.ok) throw new Error(await parseError(res));
    return res.json();
};

export const loginUser = async ({ email, password }) => {
    const res = await fetch(`${API_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
    });
    if (!res.ok) throw new Error(await parseError(res));
    return res.json(); // Beklenen: { accessToken, walletId (varsa) veya kullanıcı bilgisi }
};

export const getMyAccount = async (token) => {
    const res = await fetch(`${API_URL}/account/me`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!res.ok) throw new Error(await parseError(res));

    return res.json();
};
export const getWallet = async (walletId, token) => {
    const res = await fetch(`${API_URL}/wallets/${walletId}`, {
        headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error(await parseError(res));
    return res.json();
};

export const getTransactions = async (walletId, token) => {
    const res = await fetch(`${API_URL}/wallets/${walletId}/transactions`, {
        headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error(await parseError(res));
    return res.json();
};
export const transferByEmail = async (
    { targetEmail, amount, currency },
    token
) => {
    const res = await fetch(`${API_URL}/account/transfer`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
            'Idempotency-Key': crypto.randomUUID(),
        },
        body: JSON.stringify({
            targetEmail,
            amount: Number(amount),
            currency: currency || 'TRY',
        }),
    });

    if (!res.ok) {
        throw new Error(await parseError(res));
    }

    return null;
};
export const transferMoney = async (
    walletId,
    { targetWalletId, amount, currency },
    token
) => {
    const res = await fetch(`${API_URL}/wallets/${walletId}/transfer`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
            'Idempotency-Key': crypto.randomUUID(),
        },
        body: JSON.stringify({
            targetWalletId,
            amount: Number(amount),
            currency: currency || 'TRY',
        }),
    });

    if (!res.ok) throw new Error(await parseError(res));

    return res.json();
};
export const depositMoney = async (walletId, { amount, currency }, token) => {
    const res = await fetch(`${API_URL}/wallets/${walletId}/deposit`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
            'Idempotency-Key': crypto.randomUUID(),
        },
        body: JSON.stringify({ amount: Number(amount), currency }),
    });
    if (!res.ok) throw new Error(await parseError(res));
    return res.json();
};

export const withdrawMoney = async (walletId, { amount, currency }, token) => {
    const res = await fetch(`${API_URL}/wallets/${walletId}/withdraw`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
            'Idempotency-Key': crypto.randomUUID(),
        },
        body: JSON.stringify({ amount: Number(amount), currency }),
    });
    if (!res.ok) throw new Error(await parseError(res));
    return res.json();
};