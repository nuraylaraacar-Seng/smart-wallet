import { theme } from '../theme';
import { Skeleton } from './Skeleton';

export default function WalletCard({ balance, currency, walletId, loading }) {
    const maskedId = walletId ? maskWalletId(walletId) : '•••• •••• •••• ••••';

    return (
        <div
            style={{
                position: 'relative',
                background: 'linear-gradient(155deg, #1C2119 0%, #121611 55%, #0A0D0A 100%)',
                borderRadius: 24,
                padding: '28px 26px',
                color: theme.textPrimary,
                border: `1px solid ${theme.border}`,
                overflow: 'hidden',
                animation: 'sw-fade-up 0.4s ease',
            }}
        >
            {/* dekoratif doku: tek, sakin bir gradient halka */}
            <div
                style={{
                    position: 'absolute',
                    top: -60,
                    right: -60,
                    width: 200,
                    height: 200,
                    borderRadius: '50%',
                    background: `radial-gradient(circle, ${theme.mintSoft} 0%, transparent 70%)`,
                    pointerEvents: 'none',
                }}
            />

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 36 }}>
                <span style={{ fontSize: 12.5, color: theme.textMuted, letterSpacing: 0.2 }}>
                    Kullanılabilir bakiye
                </span>
                <span
                    style={{
                        fontSize: 11,
                        fontWeight: 600,
                        fontFamily: theme.fontDisplay,
                        color: theme.mint,
                        background: theme.mintSoft,
                        padding: '4px 10px',
                        borderRadius: 100,
                    }}
                >
                    SW
                </span>
            </div>

            {loading ? (
                <Skeleton width={200} height={38} radius={8} style={{ marginBottom: 30, opacity: 0.12 }} />
            ) : (
                <p style={{ fontFamily: theme.fontDisplay, fontSize: 36, fontWeight: 600, margin: '0 0 30px', letterSpacing: -0.5 }}>
                    {formatAmount(balance)}
                    <span style={{ fontSize: 16, fontWeight: 500, color: theme.textMuted, marginLeft: 8 }}>{currency}</span>
                </p>
            )}

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
                <div>
                    <span style={{ display: 'block', fontSize: 10.5, color: theme.textMuted, marginBottom: 4 }}>
                        Cüzdan numarası
                    </span>
                    {loading ? (
                        <Skeleton width={160} height={14} radius={4} style={{ opacity: 0.12 }} />
                    ) : (
                        <span style={{ fontFamily: theme.fontMono, fontSize: 13, color: theme.textSecondary, letterSpacing: 0.5 }}>
                            {maskedId}
                        </span>
                    )}
                </div>
                <svg width="32" height="22" viewBox="0 0 32 22" fill="none">
                    <circle cx="11" cy="11" r="11" fill={theme.mint} opacity="0.85" />
                    <circle cx="21" cy="11" r="11" fill={theme.textSecondary} opacity="0.35" />
                </svg>
            </div>
        </div>
    );
}

function maskWalletId(id) {
    const clean = id.replace(/-/g, '');
    const last4 = clean.slice(-4);
    return `•••• •••• •••• ${last4}`;
}

function formatAmount(value) {
    if (value === null || value === undefined) return '—';
    return new Intl.NumberFormat('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value);
}
