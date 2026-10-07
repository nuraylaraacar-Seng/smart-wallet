import { theme } from '../theme';
import { Skeleton } from './Skeleton';

export default function Profile({ email, iban, walletId, onLogout, loading }) {
    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 18, animation: 'sw-fade-up 0.35s ease' }}>
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '1.5rem 0 0.5rem' }}>
                <div
                    style={{
                        width: 64,
                        height: 64,
                        borderRadius: '50%',
                        background: theme.mintSoft,
                        color: theme.mint,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: 24,
                        fontWeight: 600,
                        fontFamily: theme.fontDisplay,
                        marginBottom: 12,
                    }}
                >
                    {(email || 'U').charAt(0).toUpperCase()}
                </div>
                {loading ? (
                    <Skeleton width={140} height={14} radius={4} />
                ) : (
                    <span style={{ fontSize: 14.5, fontWeight: 600, color: theme.textPrimary }}>{email}</span>
                )}
            </div>

            <InfoCard
                rows={[
                    { label: 'E-posta', value: email, loading },
                    { label: 'IBAN', value: iban, mono: true, loading },
                    { label: 'Cüzdan ID', value: walletId, mono: true, loading },
                ]}
            />

            <div
                style={{
                    background: theme.surfaceCard,
                    border: `1px solid ${theme.border}`,
                    borderRadius: theme.radius,
                    overflow: 'hidden',
                }}
            >
                <SettingsRow label="Güvenlik ve giriş" sub="Şifre, oturum geçmişi" />
                <SettingsRow label="Bildirim tercihleri" sub="İşlem ve güvenlik uyarıları" last />
            </div>

            <button
                onClick={onLogout}
                style={{
                    width: '100%',
                    padding: '13px',
                    fontSize: 13.5,
                    fontWeight: 500,
                    borderRadius: theme.radiusSm,
                    border: `1px solid ${theme.border}`,
                    background: 'transparent',
                    color: theme.danger,
                    cursor: 'pointer',
                }}
            >
                Oturumu kapat
            </button>
        </div>
    );
}

function InfoCard({ rows }) {
    return (
        <div
            style={{
                background: theme.surfaceCard,
                border: `1px solid ${theme.border}`,
                borderRadius: theme.radius,
                padding: '0.25rem 1.25rem',
            }}
        >
            {rows.map((row, i) => (
                <div
                    key={row.label}
                    style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        padding: '14px 0',
                        borderBottom: i === rows.length - 1 ? 'none' : `1px solid ${theme.border}`,
                        gap: 16,
                    }}
                >
                    <span style={{ fontSize: 12.5, color: theme.textMuted, flexShrink: 0 }}>{row.label}</span>
                    {row.loading ? (
                        <Skeleton width={120} height={13} radius={4} />
                    ) : (
                        <span
                            style={{
                                fontSize: 13,
                                color: theme.textPrimary,
                                fontFamily: row.mono ? theme.fontMono : theme.fontBody,
                                textAlign: 'right',
                                overflow: 'hidden',
                                textOverflow: 'ellipsis',
                                whiteSpace: 'nowrap',
                            }}
                        >
                            {row.value || '—'}
                        </span>
                    )}
                </div>
            ))}
        </div>
    );
}

function SettingsRow({ label, sub, last }) {
    return (
        <div
            style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                padding: '14px 1.25rem',
                borderBottom: last ? 'none' : `1px solid ${theme.border}`,
                cursor: 'pointer',
            }}
        >
            <div>
                <p style={{ fontSize: 13, color: theme.textPrimary, margin: 0, fontWeight: 500 }}>{label}</p>
                <p style={{ fontSize: 11.5, color: theme.textMuted, margin: '2px 0 0' }}>{sub}</p>
            </div>
            <span style={{ color: theme.textMuted, fontSize: 16 }}>›</span>
        </div>
    );
}
