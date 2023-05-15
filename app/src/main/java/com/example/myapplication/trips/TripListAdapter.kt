package com.example.myapplication.trips

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Utils.TRIP_INFO
import com.example.myapplication.databinding.ViewTripListItemBinding

class TripListAdapter(var item: List<Trip>) : RecyclerView.Adapter<TripListAdapter.UserHolder>() {

    class UserHolder(private val binding: ViewTripListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(trip: Trip) {
            binding.txtDepartureInfo.text = trip.departure
            binding.txtDestinationInfo.text = trip.destination
            binding.txtSeats.text = trip.availableSeats
            binding.txtDate.text = buildString {
                this.append(trip.date)
                this.append(", ")
                this.append(trip.time) }
            binding.txtPrice.text = buildString {
                append(trip.price)
                append(" €") }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val binding = ViewTripListItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return UserHolder(binding)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        val context = holder.itemView.context
        holder.bind(item[position])
        holder.itemView.setOnClickListener {
            context.startActivity(
                Intent(context, InfoTripActivity::class.java)
                    .putExtra(TRIP_INFO, item[position])
            )
        }
    }

    override fun getItemCount(): Int {
        return item.size
    }


}