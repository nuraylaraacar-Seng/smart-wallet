export function Skeleton({ width = '100%', height = 16, radius = 6, style = {} }) {
    return (
        <div
            style={{
                width,
                height,
                borderRadius: radius,
                background: 'linear-gradient(90deg, #1A1F18 25%, #242A22 37%, #1A1F18 63%)',
                backgroundSize: '400% 100%',
                animation: 'sw-skeleton 1.4s ease infinite',
                ...style,
            }}
        />
    );
}
