package com.example.tpdm_u3_practica2_maldonado;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Map;

public class Main3Activity extends AppCompatActivity {
    EditText nombre, area, titulo,id;
    Button insertar, eliminar,consultar,actualizar;

    ListView listado;

    FirebaseFirestore servicioBaseDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        nombre = findViewById(R.id.nombreMa);
        area = findViewById(R.id.areaMa);
        titulo = findViewById(R.id.tituloMa);
        id = findViewById(R.id.idMa);

        insertar = findViewById(R.id.insertarMa);
        eliminar = findViewById(R.id.eliminarMa);
        //actualizar = findViewById(R.id.actualizarEst);
        consultar = findViewById(R.id.consultarMa);

        listado = findViewById(R.id.listMa);
        servicioBaseDatos = FirebaseFirestore.getInstance();
        insertar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insertarAlumnoAutoID();
                insertarMa();
            }
        });

        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarMa();
            }
        });

        consultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //consultarTodos();
                consultarMa();
            }
        });
    }
    private void consultarMa() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText matriculaBusc = new EditText(this);
        matriculaBusc.setInputType(InputType.TYPE_CLASS_NUMBER);
        matriculaBusc.setHint("id");

        alerta.setTitle("BUSQUEDA").setMessage("ESCRIBA EL ID")
                .setView(matriculaBusc)
                .setPositiveButton("buscar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(matriculaBusc.getText().toString().isEmpty()){
                            Toast.makeText(Main3Activity.this,
                                    "DEBES PONER UNA MATRICULA", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        consultarEst(matriculaBusc.getText().toString());
                    }
                })
                .setNegativeButton("cancelar",null)
                .show();
    }
    private void consultarEst(String matriculaBuscae){
        servicioBaseDatos.collection("maestro")
                .whereEqualTo("id",matriculaBuscae)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        Query q = queryDocumentSnapshots.getQuery();

                        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot registro : task.getResult()){
                                        Map<String, Object> dato = registro.getData();

                                        nombre.setText(dato.get("nombre").toString());
                                        id.setText(dato.get("id").toString());
                                        area.setText(dato.get("area").toString());
                                        titulo.setText(dato.get("titulo").toString());
                                    }
                                }
                                //hacer else de tarea
                            }
                        });

                    }
                });
    }

    private void eliminarEst() {
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        final EditText idEliminar = new EditText(this);
        idEliminar.setHint("NO DEBE QUEDAR VACIO");

        alerta.setTitle("ATENCION").setMessage("ESCRIBA EL ID:")
                .setView(idEliminar)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(idEliminar.getText().toString().isEmpty()){
                            Toast.makeText(Main3Activity.this, "EL ID ESTA VACIO",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        eliminarAlumnos2(idEliminar.getText().toString());
                    }
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }
    private void eliminarAlumnos2(String idEliminar){
        servicioBaseDatos.collection("maestro")
                .document(idEliminar)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main3Activity.this,
                                "SE ELIMINO!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main3Activity.this,
                                "NO SE ENCONTRO COINCIDENCIA!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void insertarEst() {
        Maestro ma = new Maestro(area.getText().toString(), id.getText().toString(),  nombre.getText().toString(), titulo.getText().toString());

        servicioBaseDatos.collection("maestro")
                .document(id.getText().toString())
                .set(ma)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Main3Activity.this, "SE INSERTO CORRECTAMENTE",
                                Toast.LENGTH_SHORT).show();
                        nombre.setText("");area.setText("");titulo.setText("");id.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Main3Activity.this,
                                "ERROR NO SE PUDO INSERTAR", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
