package com.earth.angel.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


open class BaseViewHolder<VB : ViewDataBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)