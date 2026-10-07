import { theme } from '../theme';
import Logo from './Logo';

export default function Topbar({ title }) {
    return (
        <div
            style={{
                position: 'sticky',
                top: 0,
                display: 'flex',
                alignItems: 'center',
                gap: 10,
                padding: 'calc(14px + env(safe-area-inset-top, 0px)) 20px 14px',
                background: 'rgba(10,13,10,0.85)',
                backdropFilter: 'blur(14px)',
                borderBottom: `1px solid ${theme.border}`,
                zIndex: 10,
            }}
        >
            <Logo size={28} />
            <span style={{ fontSize: 15, fontWeight: 600, color: theme.textPrimary, fontFamily: theme.fontDisplay }}>
                {title || 'Smart Wallet'}
            </span>
        </div>
    );
}
