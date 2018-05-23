   //Universidade Estadual do Maranhão
   //Marcos Vinicius de Oliveira Guilhon Rosa - 201432534
   //Programação de dispositivos móveis


package titopetri.com.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
//Classe Main que extende da classe que gera o ciclo de vida, pegando todos os objetos//
public class MainActivity extends Activity  {

    private Button meuBotao;
    private ListView minhaLista;
    private EditText meuTexto;

    private SQLiteDatabase bancoDados;

    private ArrayAdapter<String> itensAdaptador;
    private ArrayList<String> itens;
    private ArrayList<Integer> ids;

    @Override
    //
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        meuTexto = (EditText) findViewById(R.id.editText);
        meuBotao = (Button) findViewById(R.id.button);
        minhaLista = (ListView) findViewById(R.id.listView);

        minhaLista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            //chamando a classe alertaApagaTarefa para remover uma tarefa com o metodo LONGCLICK
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Log.i("LogX", position + " / " + ids.get(position));
                alertaApagaTarefa(position);
                return false;
            }
        });
        carregaTarefas();
    }
    //classe para adicionar novas tarefas na lista
    private void gravarNovaTarefa(String tarefa){
        try{
            if(tarefa.equals("")){
                Toast.makeText(MainActivity.this, "Insira uma tarefa!", Toast.LENGTH_SHORT).show();
            }else{
                bancoDados.execSQL("INSERT INTO minhastarefas(tarefa) VALUES('" + tarefa + "')");
                Toast.makeText(MainActivity.this, "Tarefa Salva!", Toast.LENGTH_SHORT).show();
                meuTexto.setText("");
                carregaTarefas();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
      //usando o try/catch para evitar erros de conversão ou comandos.. Classe que carrega tabela
    private void carregaTarefas(){
        try{
            //criação do banco com o nome"bancoDados", criação de tabela "minhastarefas"
            bancoDados = openOrCreateDatabase("ToDoListApp", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS minhastarefas(id INTEGER PRIMARY KEY AUTOINCREMENT, tarefa VARCHAR)");

            meuBotao.setOnClickListener(new View.OnClickListener() {
                @Override
                //Classe que serve para add nova tarefa
                public void onClick(View v) {
                     //Pegando o valor em string do que esta no texto inserido e depois insere no banco com o GravarNovaTarefa
                    String novaTarefa = meuTexto.getText().toString();
                    gravarNovaTarefa(novaTarefa);
                }
            });



            //Cursor para percorrer a tabela com o rawQuery ordenando os ID decrescente
            Cursor cursor = bancoDados.rawQuery("SELECT * FROM minhastarefas ORDER BY id DESC", null);
            //criando dois numeros com ID e TAREFA
            int indiceColunaID = cursor.getColumnIndex("id");
            int indiceColunaTarefa = cursor.getColumnIndex("tarefa");

            itens = new ArrayList<String>();
            ids  = new ArrayList<Integer>();

            itensAdaptador = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text1,
                    itens);
            minhaLista.setAdapter(itensAdaptador);

            cursor.moveToFirst();
             //Fazendo um loop para exibir ID e TAREFA. pegando o texto que tá no numero indice e coluna ID, imprimindo resultado por resultado
            while (cursor != null){
                Log.i("LogX", "ID: " + cursor.getString(indiceColunaID) + "Tarefa: " + cursor.getString(indiceColunaTarefa));
                itens.add(cursor.getString(indiceColunaTarefa));
                ids.add(Integer.parseInt(cursor.getString(indiceColunaID)));

                cursor.moveToNext();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //Classe para remover uma tarefa com o DELETE FROM..
    private void removerTarefa(Integer id){
        try{
            bancoDados.execSQL("DELETE FROM minhastarefas WHERE id="+id);
            carregaTarefas();
            Toast.makeText(MainActivity.this, "Tarefa Removida!", Toast.LENGTH_SHORT).show();

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void alertaApagaTarefa(Integer idSelecionado){

        String tarefaSelecionada = itens.get(idSelecionado);
        final Integer numerodoid = idSelecionado;

        new AlertDialog.Builder(MainActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Alerta!")
                .setMessage("Deseja apagar a tarefa: " + tarefaSelecionada + " ?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removerTarefa(ids.get(numerodoid));
                    }
                }).setNegativeButton("Não", null).show();
    }
}
