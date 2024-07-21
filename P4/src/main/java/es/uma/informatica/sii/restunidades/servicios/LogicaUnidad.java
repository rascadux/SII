package es.uma.informatica.sii.restunidades.servicios;

import es.uma.informatica.sii.restunidades.entidades.UnidadDocente;
import es.uma.informatica.sii.restunidades.excepciones.UnidadExistenteException;
import es.uma.informatica.sii.restunidades.excepciones.UnidadNoEncontrada;
import es.uma.informatica.sii.restunidades.repositorios.UnidadRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LogicaUnidad {

	private UnidadRepo repo;

	@Autowired
	public LogicaUnidad(UnidadRepo repo) {
		this.repo=repo;
	}

	public List<UnidadDocente> getTodasUnidades() {
		return repo.findAll();
	}

	// TODO

	public UnidadDocente aniadirUnidad(UnidadDocente unidad) throws UnidadExistenteException {
		Optional<UnidadDocente> u = repo.findByCursoAndGrupo(unidad.getCurso(), unidad.getGrupo());
		if (u.isPresent()) {
			throw new UnidadExistenteException();
		}
		return repo.save(unidad);
	}

	public UnidadDocente getUnidad(Long id) throws UnidadNoEncontrada {
		Optional<UnidadDocente> u = repo.findById(id);
		if (u.isPresent()) {
			return u.get();
		} else {
			throw new UnidadNoEncontrada();
		}
	}

	public UnidadDocente modificarUnidad(Long id, UnidadDocente unidad) throws UnidadNoEncontrada {
		Optional<UnidadDocente> u = repo.findById(id);
		if (!u.isPresent()) {
			throw new UnidadNoEncontrada();
		}
		Optional<UnidadDocente> uExists = repo.findByCursoAndGrupo(unidad.getCurso(),
				unidad.getGrupo());
		if (uExists.isPresent()) {
			UnidadDocente uMod = u.get();
			if(!uMod.getCurso().equals(unidad.getCurso()) && !uMod.getGrupo().equals(unidad.getGrupo())){
				throw new UnidadExistenteException();
			}
		}
		if(unidad.equals(u.get())){
			throw new UnidadExistenteException();
		}
		UnidadDocente uMod = u.get();
		uMod.setCurso(unidad.getCurso());
		uMod.setGrupo(unidad.getGrupo());
		uMod.setAula(unidad.getAula());
		return repo.save(uMod);

	}

	public void borrarUnidad(Long id) throws UnidadNoEncontrada {
		Optional<UnidadDocente> u = repo.findById(id);
		if (u.isPresent()) {
			repo.delete(u.get());
		} else {
			throw new UnidadNoEncontrada();
		}
	}
}
