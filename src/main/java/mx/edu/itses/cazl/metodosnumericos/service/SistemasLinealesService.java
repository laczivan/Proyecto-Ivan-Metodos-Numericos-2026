package mx.edu.itses.cazl.metodosnumericos.service;

import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales.DeterminanteRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales.EliminacionGaussianaRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales.GaussJordanRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales.GaussSeidelRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.request.sistemaslineales.JacobiRequest;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.DeterminanteRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.EliminacionGaussianaRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.GaussJordanRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.GaussSeidelRespuesta;
import mx.edu.itses.cazl.metodosnumericos.dto.response.sistemaslineales.JacobiRespuesta;

public interface SistemasLinealesService {
    DeterminanteRespuesta calcularDeterminante(DeterminanteRequest request);
    EliminacionGaussianaRespuesta eliminacionGaussiana(EliminacionGaussianaRequest request);
    GaussJordanRespuesta gaussJordan(GaussJordanRequest request);
    JacobiRespuesta jacobi(JacobiRequest request);
    GaussSeidelRespuesta gaussSeidel(GaussSeidelRequest request);
}