package com.rhinopacking.DB;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Log;

import com.rhinopacking.models.Operador;
import com.rhinopacking.models.Registro;
import com.rhinopacking.models.RegistroPaquete;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQL {
    private static Connection mConection=null;


    private final static String ip = "20.164.38.146";
    private final static String puerto = "1433";
    private final static String user = "sa";
    private final static String password = "jeenmage12A";
    private final static String dbName = "RhinoPacking";
    private final static String extras = "";

//LOCAL


    public void connect()
    {
        try{
            if(mConection==null || mConection.isClosed())
            {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                mConection = DriverManager.getConnection("jdbc:jtds:sqlserver://"+ ip + ":"+puerto+";DatabaseName="+dbName+";user="+user+";password=" + password + extras);
                //mConection = DriverManager.getConnection("jdbc:jtds:sqlserver://192.168.100.2:1433;DatabaseName=RhinoPacking;user=sa;password=asd123");

            }
        }catch(Exception e){ Log.d("DEPURACION", "ERROR: " + e);}
    }

    public void close(){
        try{
            mConection.close();
        }catch(Exception e){ Log.d("DEPURACION", "ERROR: " + e);}
    }



    private boolean finalizado;
    private List<Registro> mRegistros;

    public List<Registro> getRegistros() {
        return mRegistros;
    }

    public List<RegistroPaquete> mRegistrosPaquetes;

    public List<Operador> getmOperadores() {
        return mOperadores;
    }

    List<Operador> mOperadores;

    public List<RegistroPaquete> getmRegistrosPaquetes() {
        return mRegistrosPaquetes;
    }

    public boolean consultarRegistros()
    {
        mRegistros= new ArrayList<>();
        Registro registro;
        ResultSet rs;
        Statement stm;
        try{
            connect();

            stm = mConection.createStatement();
            rs = stm.executeQuery("SELECT * FROM dbo.registros");

            while(rs.next()){
                registro = new Registro(rs.getString("codigo"), rs.getString("nombre"), rs.getString("status"),rs.getBoolean("pagado"), rs.getDate("fecha_almacen"), rs.getBoolean("activo"), rs.getFloat("precio"), rs.getString("telefono"), rs.getInt("id_operador"));
                mRegistros.add(registro);
            }

            return true;
        }catch (Exception e){
            return false;
        }
    }

    Registro registro;

    public Registro getRegistro() {
        return registro;
    }

    public boolean consultarRegistro(String codigo)
    {
        registro = new Registro();
        ResultSet rs;
        Statement stm;
        try{
            connect();

            stm = mConection.createStatement();
            rs = stm.executeQuery("SELECT * FROM dbo.registros WHERE dbo.registros.codigo = '"+codigo+"'");




            while(rs.next()){
                //registro = new Registro(rs.getString("codigo"), rs.getString("nombre"), rs.getString("status"),rs.getBoolean("pagado"), rs.getDate("fecha_almacen"), rs.getBoolean("activo"), rs.getFloat("precio"), rs.getString("telefono"));
                registro.setStatus(rs.getString("status"));
                registro.setPagado(rs.getBoolean("pagado"));
                registro.setPrecio(rs.getFloat("precio"));
                registro.setTelefono(rs.getString("telefono"));

                byte[] bytes = rs.getBytes("foto_recibido");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                registro.setFoto_recibido(bitmap);
                registro.setFecha_almacen(rs.getTimestamp("fecha_almacen"));
                registro.setFecha_entrega(rs.getTimestamp("fecha_entrega"));

                registro.setNombre(rs.getString("nombre"));
                registro.setTelefono(rs.getString("telefono"));

                registro.setPrecio(rs.getFloat("precio"));

                registro.setPagado(rs.getBoolean("pagado"));
                registro.setMetodo(rs.getInt("metodo_pago"));
                registro.setObservaciones(rs.getString("observaciones"));
                registro.setId_operador(rs.getInt("id_operador"));
                registro.setActivo(rs.getBoolean("activo"));



                if(rs.getString("status").equals("FINALIZADO"))
                {
                    bytes = rs.getBytes("foto_entrega");
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    registro.setFoto_entrega(bitmap);
                }

                return true;
            }

            return false;
        }catch (Exception e){
            return false;
        }
    }



    public boolean consultarPaquetes(String codigo)
    {
        mRegistrosPaquetes = new ArrayList<>();
        RegistroPaquete registroPaquete;
        ResultSet rs;
        Statement stm;
        try{
            connect();

            stm = mConection.createStatement();
            rs = stm.executeQuery("SELECT * FROM dbo.registros_paquete WHERE codigo='"+codigo+"'");

            while(rs.next()){
                byte[] bytes = rs.getBytes("foto");
                index = rs.getInt("id_paquete");
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);




                registroPaquete = new RegistroPaquete(index, codigo, rs.getInt("cantidad"), rs.getString("medidas"), bitmap);
                mRegistrosPaquetes.add(registroPaquete);
            }

            return true;
        }catch (Exception e){
            return false;
        }
    }


    Bitmap bitmap;
    int index;

    public int getIndex() {
        return index;
    }

    public boolean downloadImagen(String codigo){
        ResultSet rs;
        Statement stm;
        try
        {
            connect();
            stm = mConection.createStatement();
            rs = stm.executeQuery("SELECT id_registro, foto_recibido FROM dbo.registros WHERE codigo='"+codigo+"'");

            while(rs.next()){
                byte[] bytes = rs.getBytes("foto_recibido");
                index = rs.getInt("id_registro");
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                return true;
            }
        }catch (Exception e){ Log.d("DEPURACION", "ERROR"); return false; }
        return false;
    }

    public boolean valCodigo(String codigo)
    {
        ResultSet rs;
        Statement stm;

        try
        {
            connect();
            stm = mConection.createStatement();
            rs = stm.executeQuery("SELECT codigo FROM dbo.registros WHERE codigo = '"+codigo+"'");
            return rs.next();
        }catch(Exception e)
        {
            return false;
        }
    }



    public Bitmap getImagen()
    {
        return bitmap;
    }


    public boolean addRegistro(Registro registro, List<RegistroPaquete> paquetes){

        ResultSet rs;
        Statement stm;


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        registro.getFoto_recibido().compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytesImage = byteArrayOutputStream.toByteArray();

        try
        {
            for (int i=0;i<paquetes.size();i++) {
                if(!insertarDatosPaquete(registro.getCodigo(), paquetes.get(i)))
                {
                    return false;
                }
            }

            connect();

            String query = "INSERT INTO [RhinoPacking].[dbo].[registros] ([codigo], [nombre], [telefono], [precio], [pagado], [metodo_pago], [status], [observaciones], [activo], [foto_recibido]) " +
                    "VALUES ('"+ registro.getCodigo()+"', '"+registro.getNombre()+"', '"+registro.getTelefono()+"', '"+registro.getPrecio()+"', '"+registro.isPagado()+"', '"+ registro.getMetodo()+"', '"+registro.getStatus()+"', '"+ registro.getObservaciones()+"', '1', ?)";


            PreparedStatement preparedStatement = mConection.prepareStatement(query);
            preparedStatement.setBytes(1, bytesImage);

            preparedStatement.execute();

        }catch (Exception e){ Log.d("DEPURACION", "Error: " + e); return false; }

        return true;
    }

    public  boolean insertarDatosPaquete(String codigo, RegistroPaquete registroPaquete){

        ResultSet rs;
        Statement stm;


        try{

            connect();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap2 = registroPaquete.getFoto();
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytesImage1 = byteArrayOutputStream.toByteArray();

            String query = "INSERT INTO [RhinoPacking].[dbo].[registros_paquete] ([codigo], [cantidad], [medidas], [foto]) " +
                    "VALUES ('"+codigo+"', '"+registroPaquete.getCantidad()+"', '"+registroPaquete.getMedidas()+"',?)";

            PreparedStatement preparedStatement = mConection.prepareStatement(query);
            preparedStatement.setBytes(1, bytesImage1);

            preparedStatement.execute();

            return true;
        }catch (Exception e){ Log.d("DEPURACION", "Error: " + e); return false;}
    }




    public boolean getValOperadores()
    {
        Statement stm;
        ResultSet rs;

        Operador operador;

        mOperadores = new ArrayList<>();

        try
        {
            connect();



            stm = mConection.createStatement();
            //stm = mConection.prepareStatement("SELECT * FROM dbo.usuarios")

            rs = stm.executeQuery("SELECT * FROM dbo.usuarios");

            while(rs.next()){
                operador = new Operador(rs.getInt("id_usuario"), rs.getString("nombre"), rs.getString("telefono"));
                mOperadores.add(operador);
            }


        return true;
        }catch(Exception e)
        {
            return false;
        }
    }


    public boolean updateStatus(String codigo, int operador)
    {
        try
        {
            connect();

            String query = "UPDATE [RhinoPacking].[dbo].[registros] SET [status]='EN_CAMINO', [id_operador]='"+operador+"' WHERE ([codigo]='"+codigo+"')";

            PreparedStatement preparedStatement = mConection.prepareStatement(query);
            preparedStatement.execute();
            return true;
        }catch(Exception e) { return false; }
    }



    public String getOperador(int id_usuario){
        ResultSet rs;
        Statement stm;
        try
        {
            connect();
            stm = mConection.createStatement();
            rs = stm.executeQuery("SELECT * FROM dbo.usuarios WHERE dbo.usuarios.id_usuario = '"+id_usuario+"'");


            rs.next();
            String nombre = rs.getString("nombre");
            return nombre;
            //return ;

        }catch (Exception e){ Log.d("DEPURACION", "ERROR"); return "Error."; }
    }


    public boolean isFinalizado() {
        return finalizado;
    }

    public boolean verificarEnCamino(String contents) {

        finalizado = false;
        ResultSet rs;
        Statement stm;
        try {
            connect();
            stm = mConection.createStatement();
            rs = stm.executeQuery("SELECT dbo.registros.codigo FROM dbo.registros WHERE dbo.registros.codigo = '"+contents+"' AND dbo.registros.status = 'EN_CAMINO'");


            while(rs.next())
            {
                finalizado = true;
                return true;
            }

            return false;
        } catch (Exception e) { return false; }
    }






    public boolean getInfoFinalizar(String codigo){
        ResultSet rs;
        Statement stm;
        try {


            connect();

            stm = mConection.createStatement();

            rs = stm.executeQuery("SELECT dbo.registros.codigo, dbo.registros.nombre, dbo.registros.precio, dbo.registros.pagado, dbo.registros.metodo_pago FROM dbo.registros WHERE dbo.registros.codigo = '"+codigo+"'");


            while(rs.next())
            {
                registro = new Registro(rs.getString("codigo"), rs.getString("nombre"), rs.getFloat("precio"), rs.getBoolean("pagado"), rs.getInt("metodo_pago"));
                return true;
            }

            return false;
        } catch (Exception e) { return false; }
    }


    public boolean updateFin(String codigo, int metodo, Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytesImage = byteArrayOutputStream.toByteArray();

        try
        {
            connect();

            String query = "UPDATE [RhinoPacking].[dbo].[registros] SET [pagado]='0', [status]='FINALIZADO', [foto_entrega]=?, [metodo_pago]='"+metodo+"', [fecha_entrega]=getdate() WHERE ([codigo]='"+codigo+"')";


            if(metodo == 1 || metodo == 2 || metodo == 3)
                query = "UPDATE [RhinoPacking].[dbo].[registros] SET [pagado]='1', [status]='FINALIZADO', [foto_entrega]=?, [metodo_pago]='"+metodo+"', [fecha_entrega]=getdate() WHERE ([codigo]='"+codigo+"')";



            PreparedStatement preparedStatement = mConection.prepareStatement(query);
            preparedStatement.setBytes(1, bytesImage);
            preparedStatement.execute();
            return true;
        }catch(Exception e) { return false; }
    }

    public boolean updateFin(String codigo, Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytesImage = byteArrayOutputStream.toByteArray();

        try
        {
            connect();

            String query = "UPDATE [RhinoPacking].[dbo].[registros] SET [status]='FINALIZADO', [foto_entrega]=?, [fecha_entrega]=getdate() WHERE ([codigo]='"+codigo+"')";

            PreparedStatement preparedStatement = mConection.prepareStatement(query);
            preparedStatement.setBytes(1, bytesImage);
            preparedStatement.execute();
            return true;
        }catch(Exception e) { return false; }
    }

    public void desactivar(String codigo)
    {
        ResultSet rs;

        try
        {
            connect();
            String query = "UPDATE [RhinoPacking].[dbo].[registros] SET [activo]='0' WHERE ([codigo]='"+ codigo +"')";
            PreparedStatement preparedStatement = mConection.prepareStatement(query);
            preparedStatement.execute();

        }catch(Exception e) {}

    }

    public boolean updateDatos(String codigo, String nombre, String telefono, String costo, String observaciones)
    {
        try
        {
            connect();

            String query = "UPDATE [RhinoPacking].[dbo].[registros] SET [codigo]='"+codigo+"', [nombre]='"+nombre+"', [telefono]='"+telefono+"', [precio]='"+costo+"', [observaciones]='"+observaciones+"' WHERE ([codigo]='"+codigo+"')";
            PreparedStatement preparedStatement = mConection.prepareStatement(query);
            preparedStatement.execute();
            return true;
        }catch(Exception e) { return false; }
    }

}
