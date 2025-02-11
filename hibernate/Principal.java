/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package empresah;

import empresa.Departamentos;
import empresa.Empleados;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
/**
 *
 * @author sofia
 */
public class Principal {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SessionFactory sesion=HibernateUtil.getSessionFactory();
        Session session=sesion.openSession();
        String hql="from Departamentos";
        Query q=session.createQuery(hql);
        //Recorremos cargando todo en memoria java 
        //hacemos una unica peticion a la bd
        List <Departamentos> list=q.list();
        System.out.println(list.size());
        for(Departamentos d :list){
            System.out.println(d.getDnombre());
        }
        
        //Hacemos multiples peticiones
        System.out.println("Iterate");
        String hql2="from Departamentos";
        Query q2= session.createQuery(hql2);
        Iterator iter2=q2.iterate();
        while(iter2.hasNext()){
            Departamentos d2=(Departamentos) iter2.next();
            System.out.println(d2.getDnombre());
        }
        
        //metemos esta query en un objeto de una clase que sea capaz de sacar 
        //todos los datos
        System.out.println("Empleados del departamento 20");
        String hql3="from Empleados as e where e.departamentos.deptNo=20";
        Query q3= session.createQuery(hql3);
        Iterator iter3=q3.iterate();
        while(iter3.hasNext()){
            Empleados e=(Empleados)iter3.next();
            System.out.println(e.getApellido());
            System.out.println(e.getDepartamentos().getDnombre());
        }
        
        //Las consultas de clases no asociadas se pueden guardar en la clase 
        //Object que es superior a todas.
        //Object nos devuelve un array de objetos , el primer objeto es la 
        //clase empleados lo casteo a esa clase y el segundo la clase departamentos igual lo casteamos
        System.out.println("Join");
        String hql4="from Empleados e,Departamentos d where e.departamentos.deptNo=d.deptNo order by Apellido";
        Query q4= session.createQuery(hql4);
        Iterator iter4=q4.iterate();
        while(iter4.hasNext()){
            Object[] partes= (Object[])iter4.next();
            Empleados e= (Empleados)partes[0];
            Departamentos d= (Departamentos)partes[1];
            System.out.println("Apellido: "+e.getApellido()+" Departamento: "+d.getDnombre());
        }
        
        //Consulta con una clase creada para trabajar con ese objeto
        //Aqui tb podriamos usar Object pero estamos viendo otra opcion 
        //CReamos la clase
        System.out.println("Trabajamos con clase Totales creada en vez de Object");
        String hql5="select new empresah.Totales(d.deptNo, count(e.empNo), coalesce(avg(e.salario),0),"
                + "d.dnombre) from Empleados as e right join e.departamentos as "
                + "d group by d.deptNo, d.dnombre";
        Query q5=session.createQuery(hql5);
        Iterator iter5=q5.iterate();
        while(iter5.hasNext()){
            Totales totales= (Totales)iter5.next();
            System.out.println(totales.toString());
        }
        
        //Cuando solo tenmos un registro ->uniqueResult
        String hql6 ="select avg(em.salario) from Empleados as em";
        Query cons= session.createQuery(hql6);
        
        Double media=(Double)cons.uniqueResult();
        System.out.println("LA media del salio es: "+media);
        
        //Consulta parametizada
        System.out.println("Consulta parametizada");
        String hql7="from Empleados e where e.departamentos.deptNo =:ndep and e.oficio =:ofi";
        
        Query q7= session.createQuery(hql7);
        q7.setParameter("ndep", (byte)30);
        q7.setParameter("ofi", "VENDEDOR");
        
        List<Empleados> list7=q7.list();
        for(Empleados e:list7){
            System.out.println(e.getApellido());
        }
        System.out.println("Empleados de dpto 30");
        String hql8="from Empleados e where e.departamentos.deptNo = 30";
        Query q8=session.createQuery(hql8);
        List<Empleados> listaE= q8.list();
        for(Empleados e:listaE){
            System.out.println("Empleado: "+e.getApellido()+"\nOficio: "+e.getOficio()+"\nSalario: "+e.getSalario());
        }
        
        System.out.println("Consulta anterior parametizada");
        System.out.println("Empleados de dpto 30");
        String hql07="from Empleados e where e.departamentos.deptNo = :dnumero";
        Query q07=session.createQuery(hql07);
        q07.setParameter("dnumero", (byte)30);
        List <Empleados> listE=q07.list();
        for(Empleados emp:listE){
            System.out.println("Apellido: "+emp.getApellido()+" Oficio: "+emp.getOficio()+" Salario: "
                    +emp.getSalario());
        }
        
        System.out.println("Consulta Media Salario");
        String hql9="select avg(e.salario) from Empleados e";
        Query q9=session.createQuery(hql9);
        Double mediaE=(Double) q9.uniqueResult();
        System.out.println("La media del salario es: "+mediaE);
        
        System.out.println("Contar Empleados");
        String hql10="select count(e.empNo) from Empleados e";
        Query q10= session.createQuery(hql10);
        Long empleadosT= (Long)q10.uniqueResult();
        System.out.println("Total Empleados: "+empleadosT);
        
        System.out.println("Departamento con mayor numero de empleados");
        String hql11="select d.dnombre, count(e.empNo) as numEmpleados from Departamentos d join "
                + "d.empleadoses e group by d.deptNo, d.dnombre order by numEmpleados desc";
        Query q11=session.createQuery(hql11);
        
        List <Object[]> lista11=q11.list();
        for(Object[] object11:lista11){
            String nombreDpto=(String)object11[0];
            Long totalEmpleados=(Long) object11[1];
            
            System.out.println(""+nombreDpto+" "+totalEmpleados);
        }
        System.out.println("Empleados ordenados por departamento y, dentro de cada departamento, por apellido:");
        String hql0="from Empleados e order by e.departamentos.deptNo, e.apellido";
        Query q0=session.createQuery(hql0);
        List<Empleados> list0=q0.list();
        for(Empleados emp0:list0){
            System.out.println(emp0.getApellido()+" "+emp0.getOficio()+" "+emp0.getSalario()+" "+emp0.getFechaAlt()+" ");
        }
        System.out.println("Lista departamentos");
        String hql01="from Departamentos";
        Query q01=session.createQuery(hql01);
        List<Departamentos> list01= q01.list();
        System.out.println("Numero de dptos: "+list01.size());
        
        for(Departamentos dep01:list01){
            System.out.println("Nombre: "+dep01.getDnombre());
        }
        
        System.out.println("Consulta 02");
        String hql02="from Empleados e,Departamentos d where e.departamentos.deptNo=d.deptNo order by Apellido";
        Query q02=session.createQuery(hql02);
        List<Object[]> list02=q02.list();
        for(Object[] object02:list02){
            Empleados emp02=(Empleados) object02[0];
            Departamentos dep02=(Departamentos) object02[1];
            
            System.out.println("D: "+emp02.getDir()+"\nApellido: "+emp02.getApellido()+"\nOficio: "+emp02.getOficio()+"\nDepartamentos: "+emp02.getDepartamentos()
                    +"\nNumero Empleado: "+emp02.getEmpNo()+"\nFecha alta: "+emp02.getFechaAlt()+"\nSalario: "+emp02.getSalario()+"\nComision: "+emp02.getComision()
                    +"\nDepartamentoN: "+dep02.getDeptNo()+"\nDepartamento: "+dep02.getDnombre()+"\nLoacalidad: "+dep02.getLoc());
        }
        
        System.out.println("Consulta 03");
        String hql03="select e.departamentos.deptNo, avg(salario),count(empNo) from Empleados e group by e.departamentos.deptNo";
        Query q03=session.createQuery(hql03);
        List<Object[]> list03=q03.list();
        for(Object[] object03: list03 ){
            Byte l=(Byte)object03[0];
            Double d=(Double)object03[1];
            Long l1=(Long)object03[2];
            
            System.out.println("Numero de Dpto: "+l+"\nMedia de salario: "+d+"\nNumero de Empleados: "+l1);
        }
        
        
        System.out.println("Consulta 04");
        String hql04="select new empresah.Totales(d.deptNo, count(e.empNo), coalesce(avg(e.salario),0), d.dnombre)"
                + " from Empleados as e right join e.departamentos as d group by d.deptNo, d.dnombre";
        Query q04=session.createQuery(hql04);
        List<Totales> listTotales= q04.list();
        for(Totales t: listTotales){
            Byte nDpto= t.getNumero();
            Long cuenta= t.getCuenta();
            Double mediaS=t.getMedia();
            String nombreDpto=t.getNombre();
            
            System.out.println("Numero Dpto: "+nDpto+"\nTotal Empleados: "+cuenta+"\nMedia Salario: "+mediaS+"\nDpto: "+nombreDpto);
        }
        String hqlS="from Empleados where empNo=7369";
         
        session.close();
        sesion.close();
    }
}
