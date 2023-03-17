package com.rhinopacking.DB;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;

import com.rhinopacking.models.Operador;
import com.rhinopacking.models.Registro;
import com.rhinopacking.models.RegistroGuia;
import com.rhinopacking.models.RegistroPaquete;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQL {
    private Connection mConection=null;


    private final static String ip = "34.174.171.223";
    private final static String puerto = "1433";
    private final static String user = "sqlserver";
    private final static String password = "sqlmagage10";
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

    public List<RegistroGuia> getmRegistrosGuias() {
        return mRegistrosGuias;
    }

    public List<RegistroGuia> mRegistrosGuias;

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
            rs = stm.executeQuery("SELECT codigo, nombre, precio, status, fecha_almacen, activo, metodo_pago FROM dbo.registros");

            while(rs.next()){
                registro = new Registro(rs.getString("codigo"), rs.getString("nombre"), rs.getFloat("precio"), rs.getString("status")
                        , rs.getTimestamp("fecha_almacen"), rs.getBoolean("activo"), rs.getInt("metodo_pago"));
                mRegistros.add(registro);

            }

            return true;
        }catch (Exception e){
            Log.d("Shaka", "Error: " + e);
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
            rs = stm.executeQuery("SELECT codigo, nombre, telefono, precio, pagado, status, observaciones, fecha_almacen, fecha_entrega, activo, metodo_pago, id_operador FROM dbo.registros WHERE dbo.registros.codigo = '"+codigo+"'");

            while(rs.next()){
                registro = new Registro(rs.getString("codigo"), rs.getString("nombre"), rs.getString("telefono"), rs.getFloat("precio"), rs.getBoolean("pagado"), rs.getString("status")
                        ,rs.getString("observaciones"), rs.getTimestamp("fecha_almacen"), rs.getTimestamp("fecha_entrega"), rs.getBoolean("activo"), rs.getInt("metodo_pago"), rs.getInt("id_operador"));
                return true;
            }
            Log.d("Shaka", "ERROR");
            return false;
        }catch (Exception e){
            Log.d("Shaka", "ERROR" + e);
            return false;
        }
    }



    public boolean consultarPaquetes(String codigo)
    {
        mRegistrosPaquetes = new ArrayList<>();
        RegistroPaquete registroPaquete;
        ResultSet rs;
        ResultSet rsPaq;
        Statement stm;
        Statement stmPaq;
        try{
            connect();

            stm = mConection.createStatement();
            rs = stm.executeQuery("SELECT id_paquete, cantidad, medidas FROM dbo.registros_paquete WHERE codigo='"+codigo+"'");

            while(rs.next()){

                index = rs.getInt("id_paquete");


                //LEER O GUARDAR
                try
                {
                    bitmap = getBitmapFromDir(codigo, "PAQ", index);
                    Log.d("Shaka", "Imagen leída desde memoria");
                }
                catch (Exception e)
                {
                    try
                    {
                        stmPaq = mConection.createStatement();
                        rsPaq = stmPaq.executeQuery("SELECT foto FROM dbo.registros_paquete WHERE id_paquete='"+index+"'");


                        while(rsPaq.next())
                        {
                            byte[] bytes = rsPaq.getBytes("foto");
                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            guardarImagen(codigo, "PAQ", bitmap, index);
                            Log.d("Shaka", "Imagen leída desde SQL");

                        }
                    }catch (Exception i)
                    {
                        Log.d("Shaka", "Error: " + i);
                    }
                }


                registroPaquete = new RegistroPaquete(index, codigo, rs.getInt("cantidad"), rs.getString("medidas"), bitmap);
                mRegistrosPaquetes.add(registroPaquete);
            }

            return true;
        }catch (Exception e){
            return false;
        }
    }

    private void guardarImagen(String codigo, String nom , Bitmap bitmap, int index)
    {
        try {

            File mainDir= new File(Environment.getExternalStorageDirectory().getPath(), "RhinoPacking");
            //File mainDir= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"", "RhinoPacking");
            if (!mainDir.exists()) {
                if(mainDir.mkdirs())
                {
                    Log.d("Shaka", "Dir creado:"+ mainDir);
                }

                else
                {
                    Log.d("Shaka", "Dir No creado:"+ mainDir);
                }
            }
            else
            {
                Log.d("Shaka", "existe" + mainDir);

                String imageName = crearNombreArchivoJPG(codigo, nom, index);
                File file = new File(mainDir, imageName);
                OutputStream out;
                try {
                    out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();

                }catch (Exception e)
                {
                    Log.d("SHAKA", "Error: " + e);
                }
            }
        } catch (Exception e){}
    }

    private String crearNombreArchivoJPG(String codigo, String nom,  int index)
    {
        return codigo +"_" + nom + index + ".jpg";
    }

    private Bitmap getBitmapFromDir(String codigo, String nom, int index) throws IOException {
        //FileInputStream read = ((Activity) context).openFileInput(codigo + "_REC" + ".jpg");

        String cadena = Environment.getExternalStorageDirectory().toString() + "/RhinoPacking" + "/" + codigo + "_" + nom + index + ".jpg";
        FileInputStream read = new FileInputStream(cadena);

        int size = read.available();
        byte[] buffer = new byte[size];
        read.read(buffer);
        read.close();
        Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
        return bitmap;
    }

    Bitmap bitmapGuia;
    public boolean consultarGuias(String codigo)
    {
        mRegistrosGuias = new ArrayList<>();
        RegistroGuia registroGuia;
        ResultSet rs;
        ResultSet rsGuia;
        Statement stm;
        Statement stmGuia;
        try{
            connect();

            stm = mConection.createStatement();
            rs = stm.executeQuery("SELECT id_guia FROM dbo.registros_guia WHERE codigo='"+codigo+"'");

            while(rs.next()){
                indexGuia = rs.getInt("id_guia");



                //LEER O GUARDAR
                try
                {
                    bitmapGuia = getBitmapFromDir(codigo, "GUIA", indexGuia);
                    Log.d("Shaka", "Imagen Guía leída desde memoria");

                }
                catch (Exception e)
                {
                    try
                    {
                        stmGuia = mConection.createStatement();
                        rsGuia = stmGuia.executeQuery("SELECT foto FROM dbo.registros_paquete WHERE id_guia='"+indexGuia+"'");



                        while(rsGuia.next())
                        {
                            byte[] bytes = rs.getBytes("foto");
                            bitmapGuia = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                            guardarImagen(codigo, "GUIA", bitmapGuia, indexGuia);



                            Log.d("Shaka", "Imagen Guía leída desde SQL");
                        }

                    }catch (Exception i)
                    {
                        Log.d("Shaka", "Error: " + i);
                    }

                }

                registroGuia = new RegistroGuia(indexGuia, codigo, bitmapGuia);
                mRegistrosGuias.add(registroGuia);


            }

            return true;
        }catch (Exception e){
            return false;
        }
    }


    Bitmap bitmap;
    int index;
    int indexGuia;

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

    public boolean downloadImagenEntrega(String codigo){
        ResultSet rs;
        Statement stm;
        try
        {
            connect();
            stm = mConection.createStatement();
            rs = stm.executeQuery("SELECT id_registro, foto_entrega FROM dbo.registros WHERE codigo='"+codigo+"'");

            while(rs.next()){
                byte[] bytes = rs.getBytes("foto_entrega");
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


    public boolean addRegistro(Registro registro, List<RegistroPaquete> paquetes, List<RegistroGuia> guias){

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

            for (int i=0;i<guias.size();i++) {
                if(!insertarDatosGuia(registro.getCodigo(), guias.get(i)))
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

    public  boolean insertarDatosGuia(String codigo, RegistroGuia registroGuia){

        ResultSet rs;
        Statement stm;


        try{

            connect();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap2 = registroGuia.getFoto();
            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bytesImage1 = byteArrayOutputStream.toByteArray();

            String query = "INSERT INTO [RhinoPacking].[dbo].[registros_guia] ([codigo], [foto]) " +
                    "VALUES ('"+codigo+"',?)";

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

            rs = stm.executeQuery("SELECT id_usuario, nombre, telefono FROM dbo.usuarios");

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
            rs = stm.executeQuery("SELECT nombre FROM dbo.usuarios WHERE dbo.usuarios.id_usuario = '"+id_usuario+"'");


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
