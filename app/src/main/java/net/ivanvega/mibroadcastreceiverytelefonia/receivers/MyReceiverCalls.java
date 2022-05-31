package net.ivanvega.mibroadcastreceiverytelefonia.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import net.ivanvega.mibroadcastreceiverytelefonia.FileManager;
import net.ivanvega.mibroadcastreceiverytelefonia.MainActivity;

public class MyReceiverCalls extends BroadcastReceiver {

    String num;
    String message;
    String entrada;
    static boolean incomingCall = false;
    static String numEntrante;


    //AQUI SE REVISA SI HAY UNA LLAMADA ENTRANTE
    @Override
    public void onReceive(Context context, Intent intent) {
        num = "";
        message = "";
        TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                if(state == TelephonyManager.CALL_STATE_RINGING){
                    numEntrante = incomingNumber.substring(3);
                    incomingCall = true;
                }
                if(state == TelephonyManager.CALL_STATE_IDLE){
                    if(incomingCall){
                        incomingCall = false;
                        entrada = FileManager.readFromFile(context).replace("\n","").replace("\r","");
                        if(entrada.contains("%!%")){
                            num = entrada.split("%!%")[0];
                            message = entrada.split("%!%")[1];
                        }
                        Log.d("HOLA", "onCallStateChanged: "+num);
                        Log.d("HOLA", "onCallStateChanged: "+incomingNumber);
                        num = num.replace(" ","");
                        Toast.makeText(context, "Registrado: "+num +"\nEntrante: "+incomingNumber, Toast.LENGTH_SHORT).show();
                        //AQUI SE COMPARA EL NÚMERO ENTRANTE, Y SE ENVÍA EL MENSAJE A ESE NÚMERO
                        if(num.equals(incomingNumber)){
                            enviarSMS(incomingNumber, message,context);
                        }
                    }
                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void enviarSMS(String tel, String msj,Context context) {
        SmsManager smsManager =  SmsManager.getDefault();
        smsManager.sendTextMessage(tel,null, msj, null, null);
        Toast.makeText(context, "Mensaje enviado", Toast.LENGTH_SHORT).show();
    }
}
