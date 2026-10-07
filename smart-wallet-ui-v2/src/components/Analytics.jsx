import { theme } from '../theme';
import { Skeleton } from './Skeleton';

export default function Analytics({ transactions, loading }) {
    const stats = computeStats(transactions);

    return (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 18, animation: 'sw-fade-up 0.35s ease' }}>
            <div
                style={{
                    background: theme.surfaceCard,
                    border: `1px solid ${theme.border}`,
                    borderRadius: theme.radius,
                    padding: '1.5rem',
                }}
            >
                <p style={{ fontSize: 13.5, fontWeight: 600, margin: '0 0 2px', color: theme.textPrimary, fontFamily: theme.fontDisplay }}>
                    Hareket dağılımı
                </p>
                <p style={{ fontSize: 12, color: theme.textMuted, margin: '0 0 22px' }}>
                    Gelen ve giden tutarların oranı
                </p>

                {loading ? (
                    <Skeleton height={14} radius={7} />
                ) : stats.total === 0 ? (
                    <EmptyNote text="Henüz işlem yok, veriler burada görünecek." />
                ) : (
                    <>
                        <div style={{ display: 'flex', height: 10, borderRadius: 6, overflow: 'hidden', marginBottom: 14 }}>
                            <div style={{ width: `${stats.inPct}%`, background: theme.mint }} />
                            <div style={{ width: `${stats.outPct}%`, background: theme.danger }} />
                        </div>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <LegendItem color={theme.mint} label="Gelen" value={stats.inTotal} />
                            <LegendItem color={theme.danger} label="Giden" value={stats.outTotal} align="right" />
                        </div>
                    </>
                )}
            </div>

            <div
                style={{
                    background: theme.surfaceCard,
                    border: `1px solid ${theme.border}`,
                    borderRadius: theme.radius,
                    padding: '1.5rem',
                }}
            >
                <p style={{ fontSize: 13.5, fontWeight: 600, margin: '0 0 2px', color: theme.textPrimary, fontFamily: theme.fontDisplay }}>
                    İşlem tipine göre
                </p>
                <p style={{ fontSize: 12, color: theme.textMuted, margin: '0 0 20px' }}>
                    Transfer, yatırma ve çekme sayıları
                </p>

                {loading ? (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                        <Skeleton height={34} radius={10} />
                        <Skeleton height={34} radius={10} />
                        <Skeleton height={34} radius={10} />
                    </div>
                ) : stats.total === 0 ? (
                    <EmptyNote text="İlk işlemini yaptığında burada kırılımını göreceksin." />
                ) : (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
                        <TypeBar label="Transfer" count={stats.byType.TRANSFER} max={stats.maxType} color={theme.mint} />
                        <TypeBar label="Yatırma" count={stats.byType.DEPOSIT} max={stats.maxType} color={theme.warning} />
                        <TypeBar label="Çekme" count={stats.byType.WITHDRAW} max={stats.maxType} color={theme.danger} />
                    </div>
                )}
            </div>
        </div>
    );
}

function LegendItem({ color, label, value, align = 'left' }) {
    return (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: align === 'right' ? 'flex-end' : 'flex-start' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6, marginBottom: 3 }}>
                {align === 'left' && <Dot color={color} />}
                <span style={{ fontSize: 11.5, color: theme.textMuted }}>{label}</span>
                {align === 'right' && <Dot color={color} />}
            </div>
            <span style={{ fontSize: 14.5, fontWeight: 600, color: theme.textPrimary, fontFamily: theme.fontMono }}>
                {formatAmount(value)}
            </span>
        </div>
    );
}

function Dot({ color }) {
    return <span style={{ width: 7, height: 7, borderRadius: '50%', background: color, display: 'inline-block' }} />;
}

function TypeBar({ label, count, max, color }) {
    const pct = max > 0 ? (count / max) * 100 : 0;
    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 5 }}>
                <span style={{ fontSize: 12.5, color: theme.textSecondary }}>{label}</span>
                <span style={{ fontSize: 12.5, color: theme.textMuted, fontFamily: theme.fontMono }}>{count}</span>
            </div>
            <div style={{ height: 6, borderRadius: 4, background: theme.surfaceRaised, overflow: 'hidden' }}>
                <div style={{ width: `${pct}%`, height: '100%', background: color, borderRadius: 4, transition: 'width 0.3s ease' }} />
            </div>
        </div>
    );
}

function EmptyNote({ text }) {
    return <p style={{ fontSize: 12.5, color: theme.textMuted, textAlign: 'center', padding: '1rem 0', margin: 0 }}>{text}</p>;
}

function computeStats(transactions) {
    const byType = { TRANSFER: 0, DEPOSIT: 0, WITHDRAW: 0 };
    let inTotal = 0;
    let outTotal = 0;

    for (const tx of transactions) {
        if (byType[tx.type] !== undefined) byType[tx.type] += 1;
        const isIncoming = tx.type === 'DEPOSIT' || tx.direction === 'IN';
        if (isIncoming) inTotal += Number(tx.amount) || 0;
        else outTotal += Number(tx.amount) || 0;
    }

    const total = inTotal + outTotal;
    const maxType = Math.max(byType.TRANSFER, byType.DEPOSIT, byType.WITHDRAW, 1);

    return {
        total,
        inTotal,
        outTotal,
        inPct: total > 0 ? (inTotal / total) * 100 : 0,
        outPct: total > 0 ? (outTotal / total) * 100 : 0,
        byType,
        maxType,
    };
}

function formatAmount(value) {
    return new Intl.NumberFormat('tr-TR', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(value);
}
