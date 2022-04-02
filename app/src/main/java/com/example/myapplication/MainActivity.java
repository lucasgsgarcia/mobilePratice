package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.Tarefa;

import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener {


    EditText edTarefa, edDuracao, edData, edDataFiltro;
    ListView lista;
    Date data;
    ArrayList<Tarefa> tarefas;
    List<Integer> selecionados = new LinkedList<>();
    TarefaAdapter adapter;
    SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        if ( selecionados.contains( pos ) ) {
            selecionados.remove( new Integer( pos)  );
        } else {
            selecionados.add( pos );
        }
        adapter.notifyDataSetChanged();
    }

    class TarefaAdapter extends ArrayAdapter<Tarefa> {
        List<Tarefa> tasks;
        public TarefaAdapter( List<Tarefa> trfs) {
            super(MainActivity.this, R.layout.item_lista_tarefa,
                    trfs);
            tasks = trfs;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public View getView( int pos, View recicled, ViewGroup parent) {
            if (recicled == null) {
                recicled = getLayoutInflater().inflate(R.layout.item_lista_tarefa,
                        null);
            }
            Tarefa t = tasks.get( pos );
            ((TextView) recicled.findViewById(R.id.il_tarefa)).setText(
                    t.getDescricao());
            ((TextView) recicled.findViewById(R.id.il_quando)).setText(
                    dateFmt.format(t.getQuando()));
            ((TextView) recicled.findViewById(R.id.il_duracao)).setText(
                    String.valueOf(t.getDuracao()));
            ((CheckBox) recicled.findViewById(R.id.cb_feita)).setChecked(
                    t.isFeita() );
            if ( selecionados.contains(pos) ) {
                recicled.setBackgroundResource( android.R.color.darker_gray );
            } else {
                recicled.setBackgroundResource( android.R.color.white );
            }
            return recicled;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            tarefas = (ArrayList<Tarefa>) savedInstanceState.getSerializable("tarefas");
        }
        if (tarefas == null) {
            tarefas = new ArrayList<>();
            tarefas.add(new Tarefa("Tirar o lixo", new Date(),10, false));
            tarefas.add(new Tarefa("Comprar pão", new Date(), 30, false));
        }
        edTarefa = (EditText) findViewById(R.id.edTarefa);
        edDataFiltro = (EditText) findViewById(R.id.edDataFiltro);
        edDuracao = (EditText) findViewById(R.id.edDuracao);
        edData = (EditText) findViewById(R.id.edQuando);
        lista = (ListView) findViewById(R.id.listaTarefas);
        adapter = new TarefaAdapter(tarefas);
        lista.setChoiceMode( ListView.CHOICE_MODE_MULTIPLE );
        lista.setAdapter( adapter );
        lista.setOnItemClickListener(this);
        lista.setOnItemLongClickListener( this );
    }

    @Override
    public void onSaveInstanceState( Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("tarefas", tarefas);
    }



    public void confirmar(View v) {
        String tarefa = edTarefa.getText().toString();
        try {
            int duracao = Integer.parseInt(
                    edDuracao.getText().toString());
            Date quando = dateFmt.parse(edData.getText().toString());
            if (tarefa.trim().length() > 0) {
                tarefas.add(new Tarefa(tarefa, quando, duracao, false));
                adapter.notifyDataSetChanged();
            }
            edTarefa.setText(""); // limpar campo da tarefa
        } catch (ParseException pe) {
            Toast.makeText(this,"Data inválida.", Toast.LENGTH_SHORT).show();
        }
    }

    public void remover(View v) {
        if (lista.getChoiceMode() == AbsListView.CHOICE_MODE_SINGLE) {
            int pos = lista.getCheckedItemPosition();
            if (pos == -1) {
                Toast.makeText(this, "Selecione a tarefa!",
                        Toast.LENGTH_SHORT).show();
            } else {
                tarefas.remove(pos);
                adapter.notifyDataSetChanged();
                lista.clearChoices();
            }
        } else {
            if (lista.getCheckedItemCount() > 0) {
                SparseBooleanArray sels = lista.getCheckedItemPositions();
                List<Tarefa> aRemover = new LinkedList<Tarefa>();
                for (int i = 0; i < tarefas.size(); i++) {
                    if ( sels.get(i) ) {
                        aRemover.add( tarefas.get(i) );
                    }
                }
                tarefas.removeAll( aRemover );
                lista.clearChoices();
                selecionados.clear();
                adapter.notifyDataSetChanged();
            }
        }
    }

    public void filtrar(View v){
        List<Tarefa> listFiltrada = null;

        try {
            Date dataFilter = dateFmt.parse(edDataFiltro.getText().toString());
            System.out.println(dataFilter);
            System.out.println(tarefas.stream().filter(tarefa -> tarefa.getQuando().toString().contains(dataFilter.toString())).count());
            listFiltrada = tarefas.stream()
                        .filter(tarefa -> tarefa.getQuando().toString().contains(dataFilter.toString())).collect(Collectors.toList());

            System.out.println(adapter = new TarefaAdapter(listFiltrada));
        adapter.notifyDataSetChanged();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(listFiltrada.size());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView,
                                   View view, int pos,
                                   long id) {
        Tarefa t = tarefas.get( pos );
        t.setFeita( true );
        adapter.notifyDataSetChanged();
        return false;
    }
}