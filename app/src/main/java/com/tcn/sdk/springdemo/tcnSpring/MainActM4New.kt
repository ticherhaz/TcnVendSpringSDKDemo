package com.tcn.sdk.springdemo.tcnSpring

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.tcn.sdk.springdemo.R
import com.ys.springboard.control.TcnShareUseData
import com.ys.springboard.control.TcnVendEventResultID
import com.ys.springboard.control.TcnVendIF

class MainActM4New : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var tvLog: TextView
    private lateinit var btShip: Button
    private lateinit var btBack: Button
    private lateinit var btShip2: Button
    private lateinit var etSlot: EditText
    private lateinit var etSlot2: EditText
    private lateinit var serport: Spinner
    private lateinit var serport2: Spinner

    private val list = mutableListOf<String>()

    private val sb = StringBuilder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mainactm4)

        tvLog = findViewById(R.id.tvLog)
        btShip = findViewById(R.id.btShip)
        btBack = findViewById(R.id.btBack)
        btShip2 = findViewById(R.id.btShip2)
        etSlot = findViewById(R.id.etSlot)
        etSlot2 = findViewById(R.id.etSlot2)
        serport = findViewById(R.id.serport)
        serport2 = findViewById(R.id.serport2)

        btShip.setOnClickListener {
            testShip()
        }
        btBack.setOnClickListener {
            finish()
        }

        btShip2.setOnClickListener {
            testShip2()
        }


        val adapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1,
            list.apply {
                add("/dev/ttyS0")
                add("/dev/ttyS1")
                add("/dev/ttyS2")
                add("/dev/ttyS3")
                add("/dev/ttyS4")
            }
        )

        serport.adapter = adapter
        serport2.adapter = adapter

        serport.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                TcnVendIF.getInstance().LoggerInfoForce(
                    TAG,
                    "serport selected position : $position  value : ${list[position]}"
                )
                TcnShareUseData.getInstance().boardSerPortFirst = list[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TcnVendIF.getInstance().LoggerInfoForce(TAG, "no item selected")
                TcnShareUseData.getInstance().boardSerPortFirst = list[0]
            }
        }

        serport2.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
                TcnVendIF.getInstance().LoggerInfoForce(
                    TAG,
                    "serport selected position : $position  value : ${list[position]}"
                )
                TcnShareUseData.getInstance().boardSerPortSecond = list[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TcnVendIF.getInstance().LoggerInfoForce(TAG, "no item selected")
                TcnShareUseData.getInstance().boardSerPortSecond = list[0]
            }
        }
        val first = TcnShareUseData.getInstance().boardSerPortFirst
        if (first.isNotEmpty()) {
            serport.setSelection(list.indexOf(first))
        }
        val second = TcnShareUseData.getInstance().boardSerPortSecond
        if (second.isNotEmpty()) {
            serport2.setSelection(list.indexOf(second))
        }

        TcnVendIF.getInstance().registerListener(listener)
    }

    private fun testShip2() {
        val slot = etSlot2.text.toString().trim()
        if (slot.isNotEmpty()) {
            sb.append("testShip")
            sb.append("$slot(货道出货)\n")
            tvLog.text = sb.toString()
            TcnVendIF.getInstance().reqShipTest(slot.toInt())
        }
    }

    private fun testShip() {
        val slot = etSlot.text.toString().trim()
        if (slot.isNotEmpty()) {
            sb.append("testShip")
            sb.append("$slot(货道出货)\n")
            tvLog.text = sb.toString()
            TcnVendIF.getInstance().reqShipTest(slot.toInt())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TcnVendIF.getInstance().unregisterListener(listener)
    }

    private val listener = TcnVendIF.VendEventListener {
        TcnVendIF.getInstance().LoggerInfoForce(
            TAG,
            "eventId : ${it.m_iEventID} param1: ${it.m_lParam1} param2: ${it.m_lParam2} param3: ${it.m_lParam3} param4: ${it.m_lParam5}"
        )
        if (it.m_lParam3.toInt() == TcnVendEventResultID.SHIP_SHIPING) {
            sb.append("出货中\n\n")
            myHandler.sendEmptyMessage(1)
        } else if (it.m_lParam3.toInt() == TcnVendEventResultID.SHIP_SUCCESS) {
            sb.append("出货成功\n\n")
            myHandler.sendEmptyMessage(1)
        } else if (it.m_lParam3.toInt() == TcnVendEventResultID.SHIP_FAIL) {
            sb.append("出货失败\n\n")
            myHandler.sendEmptyMessage(1)
        }
//        when (it.m_iEventID) {
//            TcnVendEventID.COMMAND_SHIPPING -> {//出货中  commodity is dispensed successfully
//                sb.append("出货中\n\n")
//                myHandler.sendEmptyMessage(1)
//
//            }
//            TcnVendEventID.COMMAND_SHIPMENT_SUCCESS-> { //出货成功 commodity is dispensed successfully
//                sb.append("出货成功\n\n")
//                myHandler.sendEmptyMessage(1)
//            }
//            TcnVendEventID.COMMAND_SHIPMENT_FAILURE-> {//出货失败  commodity delivery failed
//                sb.append("出货失败\n\n")
//               myHandler.sendEmptyMessage(1)
//            }
//        }

    }

    private val myHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            tvLog.text = sb.toString()
        }
    }
}
