import { theme } from '../theme';
import { Skeleton } from './Skeleton';

export default function WalletCard({ balance, currency, walletId, loading }) {
    return (
        <div style={{
            background: `linear-gradient(135deg, ${theme.anthracite} 0%, ${theme.emeraldDark} 100%)`,
            borderRadius: 18,
            padding: '1.75rem 2rem',
            color: '#fff',
            boxShadow: '0 12px 32px -8px rgba(15,92,74,0.35)',
            marginBottom: 28,
        }}>
            <p style={{ fontSize: 12.5, color: 'rgba(255,255,255,0.6)', margin: '0 0 8px', letterSpacing: 0.3, textTransform: 'uppercase' }}>
                Available balance
            </p>

            {loading ? (
                <Skeleton width={220} height={40} radius={8} style={{ marginBottom: 14, opacity: 0.15 }} />
            ) : (
                <p style={{ fontSize: 38, fontWeight: 500, margin: '0 0 14px', letterSpacing: -0.5 }}>
                    {formatAmount(balance)} <span style={{ fontSize: 18, fontWeight: 400, color: 'rgba(255,255,255,0.6)' }}>{currency}</span>
                </p>
            )}

            <div style={{ display: 'flex', alignItems: 'center', gap: 8, paddingTop: 14, borderTop: '1px solid rgba(255,255,255,0.12)' }}>
                <span style={{ fontSize: 11.5, color: 'rgba(255,255,255,0.5)' }}>Wallet ID</span>
                {loading ? (
                    <Skeleton width={200} height={12} radius={4} style={{ opacity: 0.15 }} />
                ) : (
                    <span style={{ fontSize: 12, fontFamily: 'IBM Plex Mono, monospace', color: 'rgba(255,255,255,0.75)' }}>
                        {walletId}
                    </span>
                )}
            </div>
        </div>
    );
}

function formatAmount(value) {
    if (value === null || value === undefined) return '—';
    return new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value);
}