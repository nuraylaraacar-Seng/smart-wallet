import { theme } from '../theme';

const ICONS = {
    overview: (active) => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M3 10.5 10 4l7 6.5" stroke={active ? theme.mint : theme.textMuted} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
            <path d="M5 9v6.2c0 .44.36.8.8.8h8.4c.44 0 .8-.36.8-.8V9" stroke={active ? theme.mint : theme.textMuted} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    ),
    transfer: (active) => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M3 7h11M14 7l-3-3M14 7l-3 3" stroke={active ? theme.mint : theme.textMuted} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
            <path d="M17 13H6M6 13l3-3M6 13l3 3" stroke={active ? theme.mint : theme.textMuted} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
    ),
    analytics: (active) => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <rect x="3" y="11" width="3.2" height="6" rx="1" fill={active ? theme.mint : theme.textMuted} />
            <rect x="8.4" y="7" width="3.2" height="10" rx="1" fill={active ? theme.mint : theme.textMuted} />
            <rect x="13.8" y="3" width="3.2" height="14" rx="1" fill={active ? theme.mint : theme.textMuted} />
        </svg>
    ),
    history: (active) => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <path d="M10 5v5.5l4 2" stroke={active ? theme.mint : theme.textMuted} strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round" />
            <circle cx="10" cy="10" r="7.25" stroke={active ? theme.mint : theme.textMuted} strokeWidth="1.6" />
        </svg>
    ),
    profile: (active) => (
        <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
            <circle cx="10" cy="6.5" r="3.25" stroke={active ? theme.mint : theme.textMuted} strokeWidth="1.6" />
            <path d="M3.5 17c0-3.3 2.9-5.5 6.5-5.5s6.5 2.2 6.5 5.5" stroke={active ? theme.mint : theme.textMuted} strokeWidth="1.6" strokeLinecap="round" />
        </svg>
    ),
};

const ITEMS = [
    { key: 'overview', label: 'Ana Sayfa' },
    { key: 'transfer', label: 'Transfer' },
    { key: 'analytics', label: 'Analiz' },
    { key: 'history', label: 'Geçmiş' },
    { key: 'profile', label: 'Profil' },
];

export default function BottomNav({ active, onNavigate }) {
    return (
        <nav
            style={{
                position: 'sticky',
                bottom: 0,
                left: 0,
                right: 0,
                display: 'flex',
                justifyContent: 'space-around',
                padding: '10px 8px calc(10px + env(safe-area-inset-bottom, 0px))',
                background: 'rgba(18,22,17,0.92)',
                backdropFilter: 'blur(14px)',
                borderTop: `1px solid ${theme.border}`,
                zIndex: 10,
            }}
        >
            {ITEMS.map((item) => {
                const isActive = active === item.key;
                return (
                    <button
                        key={item.key}
                        onClick={() => onNavigate(item.key)}
                        style={{
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                            gap: 4,
                            background: 'none',
                            border: 'none',
                            cursor: 'pointer',
                            padding: '4px 10px',
                        }}
                    >
                        {ICONS[item.key](isActive)}
                        <span style={{ fontSize: 10.5, color: isActive ? theme.mint : theme.textMuted, fontWeight: isActive ? 600 : 400 }}>
                            {item.label}
                        </span>
                    </button>
                );
            })}
        </nav>
    );
}
