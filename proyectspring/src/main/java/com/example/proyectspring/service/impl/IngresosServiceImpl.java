package com.example.proyectspring.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyectspring.dao.IIngresosDao;
import com.example.proyectspring.dto.IngresoDTO;
import com.example.proyectspring.entity.ConfiguracionTarjeta;
import com.example.proyectspring.entity.Ingresos;
import com.example.proyectspring.entity.Usuario;
import com.example.proyectspring.service.ConfiguracionTarjetaService;
import com.example.proyectspring.service.IngresosService;

@Service
public class IngresosServiceImpl implements IngresosService {
    
    private final IIngresosDao ingresosDao;
    
    @Autowired
    private ConfiguracionTarjetaService configuracionTarjetaService;

    public IngresosServiceImpl(IIngresosDao ingresosDao) {
        this.ingresosDao = ingresosDao;
    }

    @Override
    @Transactional
    public Ingresos guardarIngreso(IngresoDTO ingresoDTO, Usuario usuario) {
        Integer numeroCuotas = ingresoDTO.getNumeroCuotas();
        if (numeroCuotas == null) {
            numeroCuotas = 1;
        }
        
        // Si es tarjeta de crédito y tiene cuotas
        if (ingresoDTO.getMetodoPago() != null && 
            ingresoDTO.getMetodoPago().startsWith("credito-") && 
            numeroCuotas > 1) {
            
            return guardarIngresoConCuotas(ingresoDTO, usuario, numeroCuotas);
        } else if (ingresoDTO.getMetodoPago() != null && 
                   ingresoDTO.getMetodoPago().startsWith("credito-")) {
            // Gasto con tarjeta de crédito sin cuotas (1 cuota)
            return guardarIngresoTarjetaCredito(ingresoDTO, usuario);
        } else {
            // Gasto normal (efectivo, débito, transferencia)
            Ingresos ingreso = new Ingresos();
            ingreso.setMonto(ingresoDTO.getMonto());
            ingreso.setDescripcion(ingresoDTO.getDescripcion());
            ingreso.setCategoria(ingresoDTO.getCategoria());
            ingreso.setMetodoPago(ingresoDTO.getMetodoPago());
            ingreso.setFecha(ingresoDTO.getFecha());
            ingreso.setUsuario(usuario);
            ingreso.setNumeroCuotas(1);
            ingreso.setCuotaActual(1);
            ingreso.setMontoOriginal(ingresoDTO.getMonto());
            
            return ingresosDao.save(ingreso);
        }
    }
    
    private Ingresos guardarIngresoTarjetaCredito(IngresoDTO ingresoDTO, Usuario usuario) {
        // Obtener nombre de la tarjeta del método de pago
        String nombreTarjeta = obtenerNombreTarjeta(ingresoDTO.getMetodoPago());
        
        // Buscar configuración de la tarjeta
        Optional<ConfiguracionTarjeta> configOpt = configuracionTarjetaService
                .obtenerConfiguracionPorUsuarioYTarjeta(usuario, nombreTarjeta);
        
        Integer diaCorte = configOpt.map(ConfiguracionTarjeta::getDiaCorte).orElse(null);
        LocalDate fechaGasto = ingresoDTO.getFecha();
        LocalDate fechaMostrar = fechaGasto;
        
        // Si la compra es en el día de corte o después, moverla al siguiente mes
        if (diaCorte != null && fechaGasto.getDayOfMonth() >= diaCorte) {
            fechaMostrar = fechaGasto.plusMonths(1).withDayOfMonth(1);
        }
        
        Ingresos ingreso = new Ingresos();
        ingreso.setMonto(ingresoDTO.getMonto());
        ingreso.setDescripcion(ingresoDTO.getDescripcion());
        ingreso.setCategoria(ingresoDTO.getCategoria());
        ingreso.setMetodoPago(ingresoDTO.getMetodoPago());
        ingreso.setFecha(fechaMostrar);
        ingreso.setUsuario(usuario);
        ingreso.setNumeroCuotas(1);
        ingreso.setCuotaActual(1);
        ingreso.setMontoOriginal(ingresoDTO.getMonto());
        ingreso.setNombreTarjeta(nombreTarjeta);
        if (diaCorte != null) {
            ingreso.setDiaCorte(diaCorte);
        }
        
        return ingresosDao.save(ingreso);
    }
    
    private Ingresos guardarIngresoConCuotas(IngresoDTO ingresoDTO, Usuario usuario, Integer numeroCuotas) {
        // Obtener nombre de la tarjeta del método de pago
        String nombreTarjeta = obtenerNombreTarjeta(ingresoDTO.getMetodoPago());
        
        // Buscar configuración de la tarjeta
        Optional<ConfiguracionTarjeta> configOpt = configuracionTarjetaService
                .obtenerConfiguracionPorUsuarioYTarjeta(usuario, nombreTarjeta);
        
        Integer diaCorte = configOpt.map(ConfiguracionTarjeta::getDiaCorte).orElse(null);
        LocalDate fechaGasto = ingresoDTO.getFecha();
        LocalDate fechaInicio = fechaGasto;
        
        // Si la compra es en el día de corte o después, moverla al siguiente mes
        if (diaCorte != null && fechaGasto.getDayOfMonth() >= diaCorte) {
            fechaInicio = fechaGasto.plusMonths(1).withDayOfMonth(1);
        }
        
        // Calcular monto por cuota
        Double montoPorCuota = ingresoDTO.getMonto() / numeroCuotas;
        
        // Crear el gasto padre (primera cuota)
        Ingresos gastoPadre = new Ingresos();
        gastoPadre.setMonto(montoPorCuota);
        gastoPadre.setDescripcion(ingresoDTO.getDescripcion() + " (Cuota 1/" + numeroCuotas + ")");
        gastoPadre.setCategoria(ingresoDTO.getCategoria());
        gastoPadre.setMetodoPago(ingresoDTO.getMetodoPago());
        gastoPadre.setFecha(fechaInicio);
        gastoPadre.setUsuario(usuario);
        gastoPadre.setNumeroCuotas(numeroCuotas);
        gastoPadre.setCuotaActual(1);
        gastoPadre.setMontoOriginal(ingresoDTO.getMonto());
        gastoPadre.setNombreTarjeta(nombreTarjeta);
        if (diaCorte != null) {
            gastoPadre.setDiaCorte(diaCorte);
        }
        
        Ingresos gastoPadreGuardado = ingresosDao.save(gastoPadre);
        
        // Crear las cuotas restantes
        for (int i = 2; i <= numeroCuotas; i++) {
            LocalDate fechaCuota = fechaInicio.plusMonths(i - 1);
            
            Ingresos cuota = new Ingresos();
            cuota.setMonto(montoPorCuota);
            cuota.setDescripcion(ingresoDTO.getDescripcion() + " (Cuota " + i + "/" + numeroCuotas + ")");
            cuota.setCategoria(ingresoDTO.getCategoria());
            cuota.setMetodoPago(ingresoDTO.getMetodoPago());
            cuota.setFecha(fechaCuota);
            cuota.setUsuario(usuario);
            cuota.setNumeroCuotas(numeroCuotas);
            cuota.setCuotaActual(i);
            cuota.setMontoOriginal(ingresoDTO.getMonto());
            cuota.setGastoPadreId(gastoPadreGuardado.getId());
            cuota.setNombreTarjeta(nombreTarjeta);
            if (diaCorte != null) {
                cuota.setDiaCorte(diaCorte);
            }
            
            ingresosDao.save(cuota);
        }
        
        return gastoPadreGuardado;
    }
    
    private String obtenerNombreTarjeta(String metodoPago) {
        if (metodoPago == null) return null;
        
        // Mapeo de método de pago a nombre de tarjeta
        String nombreBase = metodoPago.replace("credito-", "");
        
        switch (nombreBase) {
            case "banco-chile": return "Banco Chile";
            case "santander": return "Santander";
            case "bci": return "BCI";
            case "estado": return "Banco Estado";
            case "itau": return "Itaú";
            case "scotiabank": return "Scotiabank";
            case "security": return "Banco Security";
            case "falabella": return "Falabella CMR";
            case "ripley": return "Ripley";
            case "paris": return "Paris";
            case "lider": return "Líder Mastercard";
            case "presto": return "Presto";
            default: return nombreBase;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresos> obtenerIngresosPorUsuario(Usuario usuario) {
        return ingresosDao.findByUsuarioOrderByFechaDesc(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ingresos> buscarPorId(Long id) {
        return ingresosDao.findById(id);
    }

    @Override
    @Transactional
    public void eliminarIngreso(Long id) {
        ingresosDao.deleteById(id);
    }

    @Override
    @Transactional
    public Ingresos actualizarIngreso(Long id, IngresoDTO ingresoDTO) {
        Ingresos ingreso = ingresosDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Ingreso no encontrado"));
        
        ingreso.setMonto(ingresoDTO.getMonto());
        ingreso.setDescripcion(ingresoDTO.getDescripcion());
        ingreso.setCategoria(ingresoDTO.getCategoria());
        ingreso.setMetodoPago(ingresoDTO.getMetodoPago());
        ingreso.setFecha(ingresoDTO.getFecha());
        
        return ingresosDao.save(ingreso);
    }

    // Métodos legacy
    @Override
    @Transactional
    public Ingresos saveIngresos(Ingresos ingresos) {
        return ingresosDao.save(ingresos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ingresos> findAllIngresos() {
        return ingresosDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Ingresos findIngresosById(Long id) {
        return ingresosDao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void deleteIngresos(Long id) {
        ingresosDao.deleteById(id);
    }
}
