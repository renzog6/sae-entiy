/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.nex.jpa;

import ar.nex.entity.Usuario;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import ar.nex.entity.UsuarioGrupo;
import ar.nex.entity.UsuarioMenu;
import java.util.ArrayList;
import java.util.List;
import ar.nex.entity.UsuarioHistorial;
import ar.nex.jpa.exceptions.NonexistentEntityException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Renzo
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) {
        if (usuario.getUsrMenuList() == null) {
            usuario.setUsrMenuList(new ArrayList<UsuarioMenu>());
        }
        if (usuario.getUsuarioHistorialList() == null) {
            usuario.setUsuarioHistorialList(new ArrayList<UsuarioHistorial>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            UsuarioGrupo grupo = usuario.getGrupo();
            if (grupo != null) {
                grupo = em.getReference(grupo.getClass(), grupo.getIdGrupo());
                usuario.setGrupo(grupo);
            }
            List<UsuarioMenu> attachedUsrMenuList = new ArrayList<UsuarioMenu>();
            for (UsuarioMenu usrMenuListUsuarioMenuToAttach : usuario.getUsrMenuList()) {
                usrMenuListUsuarioMenuToAttach = em.getReference(usrMenuListUsuarioMenuToAttach.getClass(), usrMenuListUsuarioMenuToAttach.getIdMenu());
                attachedUsrMenuList.add(usrMenuListUsuarioMenuToAttach);
            }
            usuario.setUsrMenuList(attachedUsrMenuList);
            List<UsuarioHistorial> attachedUsuarioHistorialList = new ArrayList<UsuarioHistorial>();
            for (UsuarioHistorial usuarioHistorialListUsuarioHistorialToAttach : usuario.getUsuarioHistorialList()) {
                usuarioHistorialListUsuarioHistorialToAttach = em.getReference(usuarioHistorialListUsuarioHistorialToAttach.getClass(), usuarioHistorialListUsuarioHistorialToAttach.getIdHistorial());
                attachedUsuarioHistorialList.add(usuarioHistorialListUsuarioHistorialToAttach);
            }
            usuario.setUsuarioHistorialList(attachedUsuarioHistorialList);
            em.persist(usuario);
            if (grupo != null) {
                grupo.getUsuarioList().add(usuario);
                grupo = em.merge(grupo);
            }
            for (UsuarioMenu usrMenuListUsuarioMenu : usuario.getUsrMenuList()) {
                usrMenuListUsuarioMenu.getUsuarioList().add(usuario);
                usrMenuListUsuarioMenu = em.merge(usrMenuListUsuarioMenu);
            }
            for (UsuarioHistorial usuarioHistorialListUsuarioHistorial : usuario.getUsuarioHistorialList()) {
                Usuario oldUsuarioOfUsuarioHistorialListUsuarioHistorial = usuarioHistorialListUsuarioHistorial.getUsuario();
                usuarioHistorialListUsuarioHistorial.setUsuario(usuario);
                usuarioHistorialListUsuarioHistorial = em.merge(usuarioHistorialListUsuarioHistorial);
                if (oldUsuarioOfUsuarioHistorialListUsuarioHistorial != null) {
                    oldUsuarioOfUsuarioHistorialListUsuarioHistorial.getUsuarioHistorialList().remove(usuarioHistorialListUsuarioHistorial);
                    oldUsuarioOfUsuarioHistorialListUsuarioHistorial = em.merge(oldUsuarioOfUsuarioHistorialListUsuarioHistorial);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
            UsuarioGrupo grupoOld = persistentUsuario.getGrupo();
            UsuarioGrupo grupoNew = usuario.getGrupo();
            List<UsuarioMenu> usrMenuListOld = persistentUsuario.getUsrMenuList();
            List<UsuarioMenu> usrMenuListNew = usuario.getUsrMenuList();
            List<UsuarioHistorial> usuarioHistorialListOld = persistentUsuario.getUsuarioHistorialList();
            List<UsuarioHistorial> usuarioHistorialListNew = usuario.getUsuarioHistorialList();
            if (grupoNew != null) {
                grupoNew = em.getReference(grupoNew.getClass(), grupoNew.getIdGrupo());
                usuario.setGrupo(grupoNew);
            }
            List<UsuarioMenu> attachedUsrMenuListNew = new ArrayList<UsuarioMenu>();
            for (UsuarioMenu usrMenuListNewUsuarioMenuToAttach : usrMenuListNew) {
                usrMenuListNewUsuarioMenuToAttach = em.getReference(usrMenuListNewUsuarioMenuToAttach.getClass(), usrMenuListNewUsuarioMenuToAttach.getIdMenu());
                attachedUsrMenuListNew.add(usrMenuListNewUsuarioMenuToAttach);
            }
            usrMenuListNew = attachedUsrMenuListNew;
            usuario.setUsrMenuList(usrMenuListNew);
            List<UsuarioHistorial> attachedUsuarioHistorialListNew = new ArrayList<UsuarioHistorial>();
            for (UsuarioHistorial usuarioHistorialListNewUsuarioHistorialToAttach : usuarioHistorialListNew) {
                usuarioHistorialListNewUsuarioHistorialToAttach = em.getReference(usuarioHistorialListNewUsuarioHistorialToAttach.getClass(), usuarioHistorialListNewUsuarioHistorialToAttach.getIdHistorial());
                attachedUsuarioHistorialListNew.add(usuarioHistorialListNewUsuarioHistorialToAttach);
            }
            usuarioHistorialListNew = attachedUsuarioHistorialListNew;
            usuario.setUsuarioHistorialList(usuarioHistorialListNew);
            usuario = em.merge(usuario);
            if (grupoOld != null && !grupoOld.equals(grupoNew)) {
                grupoOld.getUsuarioList().remove(usuario);
                grupoOld = em.merge(grupoOld);
            }
            if (grupoNew != null && !grupoNew.equals(grupoOld)) {
                grupoNew.getUsuarioList().add(usuario);
                grupoNew = em.merge(grupoNew);
            }
            for (UsuarioMenu usrMenuListOldUsuarioMenu : usrMenuListOld) {
                if (!usrMenuListNew.contains(usrMenuListOldUsuarioMenu)) {
                    usrMenuListOldUsuarioMenu.getUsuarioList().remove(usuario);
                    usrMenuListOldUsuarioMenu = em.merge(usrMenuListOldUsuarioMenu);
                }
            }
            for (UsuarioMenu usrMenuListNewUsuarioMenu : usrMenuListNew) {
                if (!usrMenuListOld.contains(usrMenuListNewUsuarioMenu)) {
                    usrMenuListNewUsuarioMenu.getUsuarioList().add(usuario);
                    usrMenuListNewUsuarioMenu = em.merge(usrMenuListNewUsuarioMenu);
                }
            }
            for (UsuarioHistorial usuarioHistorialListOldUsuarioHistorial : usuarioHistorialListOld) {
                if (!usuarioHistorialListNew.contains(usuarioHistorialListOldUsuarioHistorial)) {
                    usuarioHistorialListOldUsuarioHistorial.setUsuario(null);
                    usuarioHistorialListOldUsuarioHistorial = em.merge(usuarioHistorialListOldUsuarioHistorial);
                }
            }
            for (UsuarioHistorial usuarioHistorialListNewUsuarioHistorial : usuarioHistorialListNew) {
                if (!usuarioHistorialListOld.contains(usuarioHistorialListNewUsuarioHistorial)) {
                    Usuario oldUsuarioOfUsuarioHistorialListNewUsuarioHistorial = usuarioHistorialListNewUsuarioHistorial.getUsuario();
                    usuarioHistorialListNewUsuarioHistorial.setUsuario(usuario);
                    usuarioHistorialListNewUsuarioHistorial = em.merge(usuarioHistorialListNewUsuarioHistorial);
                    if (oldUsuarioOfUsuarioHistorialListNewUsuarioHistorial != null && !oldUsuarioOfUsuarioHistorialListNewUsuarioHistorial.equals(usuario)) {
                        oldUsuarioOfUsuarioHistorialListNewUsuarioHistorial.getUsuarioHistorialList().remove(usuarioHistorialListNewUsuarioHistorial);
                        oldUsuarioOfUsuarioHistorialListNewUsuarioHistorial = em.merge(oldUsuarioOfUsuarioHistorialListNewUsuarioHistorial);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = usuario.getIdUsuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            UsuarioGrupo grupo = usuario.getGrupo();
            if (grupo != null) {
                grupo.getUsuarioList().remove(usuario);
                grupo = em.merge(grupo);
            }
            List<UsuarioMenu> usrMenuList = usuario.getUsrMenuList();
            for (UsuarioMenu usrMenuListUsuarioMenu : usrMenuList) {
                usrMenuListUsuarioMenu.getUsuarioList().remove(usuario);
                usrMenuListUsuarioMenu = em.merge(usrMenuListUsuarioMenu);
            }
            List<UsuarioHistorial> usuarioHistorialList = usuario.getUsuarioHistorialList();
            for (UsuarioHistorial usuarioHistorialListUsuarioHistorial : usuarioHistorialList) {
                usuarioHistorialListUsuarioHistorial.setUsuario(null);
                usuarioHistorialListUsuarioHistorial = em.merge(usuarioHistorialListUsuarioHistorial);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Usuario findUsuario(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
