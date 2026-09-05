export function Skeleton({ width = '100%', height = 16, radius = 6, style = {} }) {
    return (
        <div
            style={{
                width,
                height,
                borderRadius: radius,
                background: 'linear-gradient(90deg, #EDEFEA 25%, #F6F7F4 37%, #EDEFEA 63%)',
                backgroundSize: '400% 100%',
                animation: 'sw-skeleton 1.4s ease infinite',
                ...style,
            }}
        />
    );
}

export function SkeletonStyles() {
    return (
        <style>{`
            @keyframes sw-skeleton {
                0% { background-position: 100% 50%; }
                100% { background-position: 0% 50%; }
            }
        `}</style>
    );
}