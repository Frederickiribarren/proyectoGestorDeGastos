// Configuración global de Chart.js
Chart.defaults.font.family = "'Inter', 'Segoe UI', Roboto, sans-serif";
Chart.defaults.plugins.legend.display = true;
Chart.defaults.plugins.legend.position = 'bottom';

// Colores personalizados
const colors = {
    primary: '#667eea',
    success: '#38ef7d',
    danger: '#f45c43',
    warning: '#ffa726',
    info: '#4776e6',
    secondary: '#764ba2'
};

const chartColors = [
    '#667eea', '#764ba2', '#f093fb', '#4facfe', 
    '#43e97b', '#fa709a', '#fee140', '#30cfd0',
    '#a8edea', '#fed6e3', '#c471f5', '#fa709a'
];

// Función para formatear moneda
function formatearPesos(monto) {
    return new Intl.NumberFormat('es-CL', {
        style: 'currency',
        currency: 'CLP',
        minimumFractionDigits: 0
    }).format(monto);
}

// Variables para los gráficos
let chartGastosIngresos, chartCategorias, chartMetodosPago, chartTiposIngreso, chartTendencia;

// Cargar datos al iniciar
document.addEventListener('DOMContentLoaded', function() {
    cargarEstadisticas();
    cargarGraficoGastosIngresos();
    cargarGraficoCategorias();
    cargarGraficoMetodosPago();
    cargarGraficoTiposIngreso();
});

// Cargar estadísticas generales
async function cargarEstadisticas() {
    try {
        const response = await fetch('/reportes/estadisticas-mes');
        const data = await response.json();

        document.getElementById('totalGastosMes').textContent = formatearPesos(data.totalGastos);
        document.getElementById('totalIngresosMes').textContent = formatearPesos(data.totalIngresos);
        document.getElementById('ahorroMes').textContent = formatearPesos(data.ahorro);
        document.getElementById('promedioGastos').textContent = formatearPesos(data.promedioGastosDiario);
        
        document.getElementById('cantidadGastos').textContent = `${data.cantidadGastos} transacciones`;
        document.getElementById('cantidadIngresos').textContent = `${data.cantidadIngresos} transacciones`;
        document.getElementById('promedioIngresos').textContent = `Ingreso: ${formatearPesos(data.promedioIngresosDiario)}/día`;

        // Calcular porcentaje de ahorro
        const porcentaje = data.totalIngresos > 0 ? ((data.ahorro / data.totalIngresos) * 100).toFixed(1) : 0;
        document.getElementById('porcentajeAhorro').textContent = `${porcentaje}% de los ingresos`;

        // Cambiar color de la tarjeta de ahorro según sea positivo o negativo
        const ahorroCard = document.getElementById('ahorroCard');
        if (data.ahorro >= 0) {
            ahorroCard.classList.remove('negative');
            ahorroCard.classList.add('positive');
        } else {
            ahorroCard.classList.remove('positive');
            ahorroCard.classList.add('negative');
        }
    } catch (error) {
        console.error('Error al cargar estadísticas:', error);
    }
}

// Gráfico de Barras: Gastos vs Ingresos
async function cargarGraficoGastosIngresos() {
    try {
        const response = await fetch('/reportes/datos-mensuales');
        const data = await response.json();

        const ctx = document.getElementById('chartGastosIngresos').getContext('2d');
        
        if (chartGastosIngresos) {
            chartGastosIngresos.destroy();
        }

        chartGastosIngresos = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.meses,
                datasets: [
                    {
                        label: 'Gastos',
                        data: data.gastos,
                        backgroundColor: 'rgba(244, 92, 67, 0.7)',
                        borderColor: 'rgba(244, 92, 67, 1)',
                        borderWidth: 2,
                        borderRadius: 5
                    },
                    {
                        label: 'Ingresos',
                        data: data.ingresos,
                        backgroundColor: 'rgba(56, 239, 125, 0.7)',
                        borderColor: 'rgba(56, 239, 125, 1)',
                        borderWidth: 2,
                        borderRadius: 5
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top',
                        labels: {
                            padding: 15,
                            font: {
                                size: 13,
                                weight: '500'
                            }
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        titleFont: {
                            size: 14
                        },
                        bodyFont: {
                            size: 13
                        },
                        callbacks: {
                            label: function(context) {
                                return context.dataset.label + ': ' + formatearPesos(context.parsed.y);
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            callback: function(value) {
                                return '$' + value.toLocaleString('es-CL');
                            },
                            font: {
                                size: 12
                            }
                        },
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        },
                        ticks: {
                            font: {
                                size: 12
                            }
                        }
                    }
                }
            }
        });

        // Crear gráfico de tendencia con los mismos datos
        cargarGraficoTendencia(data);
    } catch (error) {
        console.error('Error al cargar gráfico de gastos/ingresos:', error);
    }
}

// Gráfico Circular: Categorías
async function cargarGraficoCategorias() {
    try {
        const response = await fetch('/reportes/categorias-gastos');
        const data = await response.json();

        if (data.labels.length === 0) {
            document.getElementById('chartCategorias').parentElement.innerHTML = 
                '<p class="text-center text-muted mt-5">No hay datos de categorías para mostrar este mes</p>';
            return;
        }

        const ctx = document.getElementById('chartCategorias').getContext('2d');
        
        if (chartCategorias) {
            chartCategorias.destroy();
        }

        const total = data.data.reduce((sum, val) => sum + val, 0);

        chartCategorias = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: data.labels,
                datasets: [{
                    data: data.data,
                    backgroundColor: chartColors,
                    borderWidth: 3,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: {
                            padding: 15,
                            font: {
                                size: 12
                            },
                            generateLabels: function(chart) {
                                const data = chart.data;
                                return data.labels.map((label, i) => {
                                    const value = data.datasets[0].data[i];
                                    const percentage = ((value / total) * 100).toFixed(1);
                                    return {
                                        text: `${label} (${percentage}%)`,
                                        fillStyle: data.datasets[0].backgroundColor[i],
                                        hidden: false,
                                        index: i
                                    };
                                });
                            }
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        callbacks: {
                            label: function(context) {
                                const value = context.parsed;
                                const percentage = ((value / total) * 100).toFixed(1);
                                return `${context.label}: ${formatearPesos(value)} (${percentage}%)`;
                            }
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error al cargar gráfico de categorías:', error);
    }
}

// Gráfico Circular: Métodos de Pago
async function cargarGraficoMetodosPago() {
    try {
        const response = await fetch('/reportes/metodos-pago');
        const data = await response.json();

        if (data.labels.length === 0) {
            document.getElementById('chartMetodosPago').parentElement.innerHTML = 
                '<p class="text-center text-muted mt-5">No hay datos de métodos de pago para mostrar este mes</p>';
            return;
        }

        const ctx = document.getElementById('chartMetodosPago').getContext('2d');
        
        if (chartMetodosPago) {
            chartMetodosPago.destroy();
        }

        const total = data.data.reduce((sum, val) => sum + val, 0);

        chartMetodosPago = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: data.labels,
                datasets: [{
                    data: data.data,
                    backgroundColor: chartColors,
                    borderWidth: 3,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: {
                            padding: 15,
                            font: {
                                size: 12
                            },
                            generateLabels: function(chart) {
                                const data = chart.data;
                                return data.labels.map((label, i) => {
                                    const value = data.datasets[0].data[i];
                                    const percentage = ((value / total) * 100).toFixed(1);
                                    return {
                                        text: `${label} (${percentage}%)`,
                                        fillStyle: data.datasets[0].backgroundColor[i],
                                        hidden: false,
                                        index: i
                                    };
                                });
                            }
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        callbacks: {
                            label: function(context) {
                                const value = context.parsed;
                                const percentage = ((value / total) * 100).toFixed(1);
                                return `${context.label}: ${formatearPesos(value)} (${percentage}%)`;
                            }
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error al cargar gráfico de métodos de pago:', error);
    }
}

// Gráfico Circular: Tipos de Ingreso
async function cargarGraficoTiposIngreso() {
    try {
        const response = await fetch('/reportes/tipos-ingreso');
        const data = await response.json();

        if (data.labels.length === 0) {
            document.getElementById('chartTiposIngreso').parentElement.innerHTML = 
                '<p class="text-center text-muted mt-5">No hay datos de tipos de ingreso para mostrar este mes</p>';
            return;
        }

        const ctx = document.getElementById('chartTiposIngreso').getContext('2d');
        
        if (chartTiposIngreso) {
            chartTiposIngreso.destroy();
        }

        const total = data.data.reduce((sum, val) => sum + val, 0);

        chartTiposIngreso = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: data.labels,
                datasets: [{
                    data: data.data,
                    backgroundColor: chartColors,
                    borderWidth: 3,
                    borderColor: '#fff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'right',
                        labels: {
                            padding: 15,
                            font: {
                                size: 12
                            },
                            generateLabels: function(chart) {
                                const data = chart.data;
                                return data.labels.map((label, i) => {
                                    const value = data.datasets[0].data[i];
                                    const percentage = ((value / total) * 100).toFixed(1);
                                    return {
                                        text: `${label} (${percentage}%)`,
                                        fillStyle: data.datasets[0].backgroundColor[i],
                                        hidden: false,
                                        index: i
                                    };
                                });
                            }
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(0, 0, 0, 0.8)',
                        padding: 12,
                        callbacks: {
                            label: function(context) {
                                const value = context.parsed;
                                const percentage = ((value / total) * 100).toFixed(1);
                                return `${context.label}: ${formatearPesos(value)} (${percentage}%)`;
                            }
                        }
                    }
                }
            }
        });
    } catch (error) {
        console.error('Error al cargar gráfico de tipos de ingreso:', error);
    }
}

// Gráfico de Línea: Tendencia de Ahorro
function cargarGraficoTendencia(dataMensual) {
    const ctx = document.getElementById('chartTendencia').getContext('2d');
    
    if (chartTendencia) {
        chartTendencia.destroy();
    }

    // Calcular ahorro mensual (ingresos - gastos)
    const ahorroData = dataMensual.ingresos.map((ingreso, index) => {
        return ingreso - dataMensual.gastos[index];
    });

    chartTendencia = new Chart(ctx, {
        type: 'line',
        data: {
            labels: dataMensual.meses,
            datasets: [{
                label: 'Ahorro Mensual',
                data: ahorroData,
                borderColor: colors.info,
                backgroundColor: 'rgba(71, 118, 230, 0.1)',
                borderWidth: 3,
                fill: true,
                tension: 0.4,
                pointRadius: 5,
                pointHoverRadius: 7,
                pointBackgroundColor: colors.info,
                pointBorderColor: '#fff',
                pointBorderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: true,
                    position: 'top',
                    labels: {
                        padding: 15,
                        font: {
                            size: 13,
                            weight: '500'
                        }
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    padding: 12,
                    callbacks: {
                        label: function(context) {
                            const value = context.parsed.y;
                            const estado = value >= 0 ? '(Positivo)' : '(Negativo)';
                            return `Ahorro: ${formatearPesos(value)} ${estado}`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    ticks: {
                        callback: function(value) {
                            return '$' + value.toLocaleString('es-CL');
                        },
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    grid: {
                        display: false
                    },
                    ticks: {
                        font: {
                            size: 12
                        }
                    }
                }
            }
        }
    });
}
