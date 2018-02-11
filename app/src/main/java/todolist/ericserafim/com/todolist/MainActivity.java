package todolist.ericserafim.com.todolist;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String NOME_BANCO = "appTarefas";
    private Button botaoAdicionar;
    private EditText textoTarefa;
    private ListView listaTarefas;
    private SQLiteDatabase bancoDados;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botaoAdicionar = (Button) findViewById(R.id.botaoAdicionarId);
        textoTarefa = (EditText) findViewById(R.id.textoId);
        listaTarefas = (ListView) findViewById(R.id.listViewId);
        listaTarefas.setLongClickable(true);

        criarBancoDados();

        botaoAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoDigitado = textoTarefa.getText().toString();
                insereTarefa(textoDigitado);
                textoTarefa.setText("");
            }
        });

        listaTarefas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                excluirTarefa(ids.get(position));
                buscarTarefas();
                return true;
            }
        });

        listaTarefas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    Log.i("Swippe", "Acionado" + String.valueOf(event.getOrientation()));
                }

                return false;
            }
        });

        buscarTarefas();
    }

    private void criarBancoDados() {
        bancoDados = openOrCreateDatabase(NOME_BANCO, MODE_PRIVATE, null);
        bancoDados.execSQL("CREATE TABLE IF NOT EXISTS tarefas (id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");
    }

    private void insereTarefa(String tarefa) {
        try {
            if (tarefa.equals("")) {
                Toast.makeText(MainActivity.this, "Digite o texto da tarefa", Toast.LENGTH_SHORT).show();
                return;
            }

            bancoDados.execSQL("INSERT INTO tarefas (tarefa) VALUES ('" + tarefa + "')");
            buscarTarefas();
            Toast.makeText(MainActivity.this, "Tarefa inserida com sucesso", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buscarTarefas() {
        try {
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM tarefas ORDER BY id DESC", null);
            int indiceColunaId = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");
            int totalRegistros = cursor.getCount();

            ids = new ArrayList<Integer>();
            itens = new ArrayList<String>();
            adapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    itens);

            listaTarefas.setAdapter(adapter);

            cursor.moveToFirst();
            while (totalRegistros > 0) {
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaId)));

                cursor.moveToNext();
                totalRegistros -= 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void excluirTarefa(Integer id) {
        try {
            bancoDados.execSQL("DELETE FROM tarefas WHERE id = " + String.valueOf(id));
            Toast.makeText(MainActivity.this, "Tarefa removida com sucesso", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
