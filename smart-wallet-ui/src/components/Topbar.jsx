import { theme } from '../theme';

export default function Topbar({ email, onLogout }) {
    const initial = (email || 'U').charAt(0).toUpperCase();

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'flex-end',
            alignItems: 'center',
            gap: 14,
            padding: '1rem 2rem',
            borderBottom: `1px solid ${theme.border}`,
            background: theme.surface,
        }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <div style={{
                    width: 32, height: 32, borderRadius: '50%',
                    background: theme.emeraldSoft, color: theme.emerald,
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontSize: 13, fontWeight: 600,
                }}>
                    {initial}
                </div>
                <span style={{ fontSize: 13.5, color: theme.textPrimary, fontWeight: 500 }}>
                    {email || 'Account'}
                </span>
            </div>
            <button
                onClick={onLogout}
                style={{
                    fontSize: 13,
                    color: theme.textMuted,
                    background: theme.page,
                    border: `1px solid ${theme.borderStrong}`,
                    padding: '7px 14px',
                    borderRadius: 9,
                    cursor: 'pointer',
                }}
            >
                Sign out
            </button>
        </div>
    );
}