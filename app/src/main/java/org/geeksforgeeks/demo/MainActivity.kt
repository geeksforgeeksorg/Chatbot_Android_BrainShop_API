package org.geeksforgeeks.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONException

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var button: FloatingActionButton
    private lateinit var editText: EditText

    // creating a variable for our volley request queue.
    private lateinit var requestQueue: RequestQueue

    // creating a variable for array list and adapter class.
    private lateinit var list: ArrayList<Model>
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        button = findViewById(R.id.sendButton)
        editText = findViewById(R.id.messageEditText)

        // below line is to initialize our request queue.
        requestQueue = Volley.newRequestQueue(this)
        requestQueue.cache.clear()

        // creating a new array list
        list = ArrayList()

        // adding on click listener for send message button.
        button.setOnClickListener(View.OnClickListener { // checking if the message entered
            // by user is empty or not.
            if (editText.getText().toString().isEmpty()) {
                // if the edit text is empty display a toast message.
                Toast.makeText(this, "Please enter your message..", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            // calling a method to send message
            // to our bot to get response.
            sendMessage(editText.getText().toString())

            // below line we are setting text in our edit text as empty
            editText.setText("")
        })

        // on below line we are initializing our adapter class and passing our array list to it.
        adapter = Adapter(list)

        // below line we are creating a variable for our linear layout manager.
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        // below line is to set layout
        // manager to our recycler view.
        recyclerView.setLayoutManager(linearLayoutManager)

        // below line we are setting
        // adapter to our recycler view.
        recyclerView.setAdapter(adapter)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sendMessage(userMsg: String) {
        // below line is to pass message to our
        // array list which is entered by the user.
        list.add(Model(userMsg, USER_KEY))
        adapter.notifyDataSetChanged()

        // url for our api call
        val url = "Enter you API URL here$userMsg"

        // creating a variable for our request queue.
        val queue = Volley.newRequestQueue(this)

        // on below line we are making a json object request for a get request and passing our url .
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // in on response method we are extracting data
                    // from json response and adding this response to our array list.
                    val botResponse = response.getString("cnt")
                    list.add(Model(botResponse, BOT_KEY))

                    // notifying our adapter as data changed.
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    e.printStackTrace()

                    // handling error response from bot.
                    list.add(Model("No response", BOT_KEY))
                    adapter.notifyDataSetChanged()
                }
            }, {
                // error handling.
                list.add(Model("Sorry no response found", BOT_KEY))
                Toast.makeText(this, "No response from the bot..", Toast.LENGTH_SHORT).show()
            })


        // at last adding json object request to our queue.
        queue.add(jsonObjectRequest)
    }

    companion object {
        const val USER_KEY = "user"
        const val BOT_KEY = "bot"
    }
}