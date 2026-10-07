import { useState } from 'react';
import { theme } from '../theme';
import {
    transferMoney,
    transferByEmail,
    depositMoney,
    withdrawMoney
} from '../services/api';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';

const MODES = [
    { key: 'transfer', label: 'Transfer' },
    { key: 'deposit', label: 'Yatır' },
    { key: 'withdraw', label: 'Çek' },
];

export default function TransferPanel({ walletId, onTransactionComplete }) {
    const { accessToken } = useAuth();
    const { showToast } = useToast();
    const [mode, setMode] = useState('transfer');
    const [recipient, setRecipient] = useState('');
    const [amount, setAmount] = useState('');
    const [currency, setCurrency] = useState('TRY');
    const [fieldError, setFieldError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setFieldError('');

        if (!walletId) {
            setFieldError('Cüzdan kimliği bulunamadı. Lütfen sayfayı yenileyin.');
            return;
        }
        if (!amount || Number(amount) <= 0) {
            setFieldError('Lütfen geçerli bir tutar girin.');
            return;
        }
        if (mode === 'transfer' && !recipient.trim()) {
            setFieldError('Alıcı bilgisi (UUID veya E-posta) zorunludur.');
            return;
        }

        setLoading(true);
        try {
            if (mode === 'transfer') {
                const recipientValue = recipient.trim();
                const isUuid = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(recipientValue);

                if (isUuid) {
                    await transferMoney(walletId, { targetWalletId: recipientValue, amount: Number(amount), currency }, accessToken);
                } else {
                    await transferByEmail({ targetEmail: recipientValue, amount: Number(amount), currency }, accessToken);
                }
            } else if (mode === 'deposit') {
                await depositMoney(walletId, { amount: Number(amount), currency }, accessToken);
            } else {
                await withdrawMoney(walletId, { amount: Number(amount), currency }, accessToken);
            }

            showToast(`${MODES.find((m) => m.key === mode).label} işlemi başarılı.`, 'success');
            setAmount('');
            setRecipient('');
            onTransactionComplete?.();
        } catch (err) {
            showToast(err.message || 'İşlem başarısız oldu.', 'error');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{ background: theme.surfaceCard, border: `1px solid ${theme.border}`, borderRadius: theme.radius, padding: '1.5rem', animation: 'sw-fade-up 0.35s ease' }}>
            <div style={{ display: 'flex', gap: 6, marginBottom: 22, background: theme.surfaceRaised, padding: 4, borderRadius: 12 }}>
                {MODES.map((m) => (
                    <button
                        key={m.key}
                        type="button"
                        onClick={() => { setMode(m.key); setFieldError(''); }}
                        style={{
                            flex: 1, padding: '10px 0', fontSize: 13, fontWeight: 500,
                            borderRadius: 9, border: 'none', cursor: 'pointer',
                            background: mode === m.key ? theme.mint : 'transparent',
                            color: mode === m.key ? '#0A0D0A' : theme.textMuted,
                            transition: 'all 0.15s ease',
                        }}
                    >
                        {m.label}
                    </button>
                ))}
            </div>

            <form onSubmit={handleSubmit}>
                {mode === 'transfer' && (
                    <div style={{ marginBottom: 16 }}>
                        <label style={labelStyle}>Alıcı cüzdan ID veya e-posta</label>
                        <input
                            type="text"
                            value={recipient}
                            onChange={(e) => setRecipient(e.target.value)}
                            placeholder="Alıcı UUID veya e-posta adresi"
                            style={inputStyle}
                        />
                    </div>
                )}

                <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 10, marginBottom: 16 }}>
                    <div>
                        <label style={labelStyle}>Tutar</label>
                        <input
                            type="number"
                            step="0.01"
                            value={amount}
                            onChange={(e) => setAmount(e.target.value)}
                            placeholder="0.00"
                            style={inputStyle}
                        />
                    </div>
                    <div>
                        <label style={labelStyle}>Para birimi</label>
                        <select value={currency} onChange={(e) => setCurrency(e.target.value)} style={inputStyle}>
                            <option value="TRY">TRY</option>
                            <option value="USD">USD</option>
                            <option value="EUR">EUR</option>
                        </select>
                    </div>
                </div>

                {fieldError && (
                    <div style={{ background: theme.dangerSoft, color: theme.danger, fontSize: 12.5, padding: '10px 12px', borderRadius: 8, marginBottom: 16 }}>
                        {fieldError}
                    </div>
                )}

                <button
                    type="submit"
                    disabled={loading}
                    style={{
                        width: '100%', padding: '13px', fontSize: 14, fontWeight: 600,
                        borderRadius: theme.radiusSm, border: 'none', cursor: loading ? 'default' : 'pointer',
                        background: loading ? theme.borderStrong : theme.mint,
                        color: loading ? theme.textMuted : '#0A0D0A',
                    }}
                >
                    {loading ? 'İşlem yapılıyor…' : `${MODES.find((m) => m.key === mode).label} işlemini tamamla`}
                </button>
            </form>
        </div>
    );
}

const labelStyle = { display: 'block', fontSize: 12.5, fontWeight: 500, color: '#AEB6A9', marginBottom: 6 };

const inputStyle = {
    width: '100%',
    boxSizing: 'border-box',
    padding: '11px 14px',
    fontSize: 13.5,
    borderRadius: 10,
    border: `1px solid ${theme.border}`,
    background: theme.surfaceRaised,
    color: theme.textPrimary,
    outline: 'none',
};
