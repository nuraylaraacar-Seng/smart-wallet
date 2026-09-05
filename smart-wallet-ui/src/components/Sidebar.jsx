import { theme } from '../theme';

const NAV_ITEMS = [
    { key: 'overview', label: 'Overview', icon: '◆' },
    { key: 'transfer', label: 'Transfer', icon: '→' },
    { key: 'history', label: 'Transaction history', icon: '≡' },
];

export default function Sidebar({ active, onNavigate }) {
    return (
        <div style={{
            width: 220,
            background: theme.anthracite,
            color: '#fff',
            padding: '1.75rem 1.25rem',
            display: 'flex',
            flexDirection: 'column',
            gap: 4,
            minHeight: '100vh',
            boxSizing: 'border-box',
        }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 36 }}>
                <div style={{ width: 32, height: 32, borderRadius: 8, background: theme.emerald, display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 600, fontSize: 13 }}>
                    SW
                </div>
                <span style={{ fontSize: 14, fontWeight: 500, letterSpacing: 0.2 }}>Smart Wallet</span>
            </div>

            {NAV_ITEMS.map((item) => {
                const isActive = active === item.key;
                return (
                    <button
                        key={item.key}
                        onClick={() => onNavigate(item.key)}
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: 10,
                            padding: '10px 12px',
                            borderRadius: 10,
                            border: 'none',
                            background: isActive ? theme.emerald : 'transparent',
                            color: isActive ? '#fff' : 'rgba(255,255,255,0.65)',
                            fontSize: 13.5,
                            fontWeight: isActive ? 500 : 400,
                            textAlign: 'left',
                            cursor: 'pointer',
                            transition: 'background 0.15s',
                        }}
                        onMouseEnter={(e) => { if (!isActive) e.currentTarget.style.background = theme.anthraciteLight; }}
                        onMouseLeave={(e) => { if (!isActive) e.currentTarget.style.background = 'transparent'; }}
                    >
                        <span style={{ fontSize: 13, width: 16, textAlign: 'center' }}>{item.icon}</span>
                        {item.label}
                    </button>
                );
            })}
        </div>
    );
}