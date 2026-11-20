package com.example.CryptoTrading.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.CryptoTrading.domain.OrderStatus;
import com.example.CryptoTrading.domain.OrderType;
import com.example.CryptoTrading.entities.Asset;
import com.example.CryptoTrading.entities.Coin;
import com.example.CryptoTrading.entities.Order;
import com.example.CryptoTrading.entities.OrderItem;
import com.example.CryptoTrading.entities.User;
import com.example.CryptoTrading.repository.OrderItemRepository;
import com.example.CryptoTrading.repository.OrderRepository;

@Service
public class OrderServiceImpl  implements OrderService
{
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private WalletService walletService;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private AssetService assetService;
	
	
	public Order createOrder(User user, OrderItem orderItem, OrderType orderType)
	{
		// BigDecimal maths avoids the rounding errors of double * double
		BigDecimal price = BigDecimal.valueOf(orderItem.getCoin().getCurrentPrice())
				.multiply(BigDecimal.valueOf(orderItem.getQuantity()))
				.setScale(2, RoundingMode.HALF_UP);
		
		Order order = new Order();
		order.setUser(user);
		order.setOrderItem(orderItem);
		order.setOrderType(orderType);
		order.setPrice(price);
		order.setTimestamp(LocalDateTime.now());
		order.setStatus(OrderStatus.PENDING);
		
		 return orderRepository.save(order);
		
	}

	public Order getOrderById(Long orderId) throws Exception 
	{
		return orderRepository.findById(orderId)
				.orElseThrow(()-> new Exception("Order not found"));
	}

	public List<Order> getAllOrderOfUser(Long userId, OrderType orderType, String assetSymbol)
	{
		return orderRepository.findByUserId(userId).stream()
				.filter(o -> orderType == null || orderType.equals(o.getOrderType()))
				.filter(o -> assetSymbol == null || assetSymbol.isBlank()
						|| (o.getOrderItem() != null && o.getOrderItem().getCoin() != null
							&& assetSymbol.equalsIgnoreCase(o.getOrderItem().getCoin().getSymbol())))
				.toList();
	}
	
	
	
	// this method need in processOrder that why we declared here..........
	
	private OrderItem createOrderItem(Coin coin, double quantity, double buyPrice, double sellPrice)
	{
		OrderItem item = new OrderItem();
		item.setCoin(coin);
		item.setQuantity(quantity);
		item.setBuyPrice(buyPrice);
		item.setSellPrice(sellPrice);
		
		return orderItemRepository.save(item);
	}
	
	
	
	
	@Transactional(rollbackFor = Exception.class)
	public Order buyAsset(Coin coin, double quantity, User user) throws Exception
	{
		if (quantity<=0)
		{
			throw new Exception("Quantity should be > 0");
		}
		
		double buyPrice = coin.getCurrentPrice();
		
		OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, 0);
		
		Order order = createOrder(user, orderItem, OrderType.BUY);
		
		orderItem.setOrder(order);
		
		walletService.payOrderPayment(order, user);
		
		order.setStatus(OrderStatus.SUCCESS);
		order.setOrderType(OrderType.BUY);
		
		Order savedOrder= orderRepository.save(order);
		
		// create asset.....
		
		Asset oldAsset = assetService.findAssetByUserIdAndCoinId(
												order.getUser().getId(), 
												order.getOrderItem().getCoin().getId());
		
		if(oldAsset == null)
		{
			assetService.createAsset(user, orderItem.getCoin(), orderItem.getQuantity());
		}
		else
		{
			assetService.updateAsset(oldAsset.getId(), quantity);
		}
		
		
		return savedOrder;
		
	}
	
	@Transactional(rollbackFor = Exception.class)
	public Order sellAsset(Coin coin, double quantity, User user) throws Exception
	{
		if (quantity<=0)
		{
			throw new Exception("Quantity should be > 0");
		}
		
		double sellPrice = coin.getCurrentPrice();
		
		Asset assetToSell = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());
		
		// check before touching the asset (previously read buyPrice first -> NullPointerException)
		if (assetToSell == null)
		{
			throw new Exception("Asset not found");
		}
		
		if (assetToSell.getQuantity() < quantity)
		{
			throw new Exception("Insufficient quantity to sell...");
		}
		
		double buyPrice = assetToSell.getBuyPrice();
		
		OrderItem orderItem = createOrderItem(coin, quantity, buyPrice, sellPrice);
		
		Order order = createOrder(user, orderItem, OrderType.SELL);
		
		orderItem.setOrder(order);
		
		walletService.payOrderPayment(order, user);
		
		Asset updatedAsset = assetService.updateAsset(assetToSell.getId(), -quantity);
		
		order.setStatus(OrderStatus.SUCCESS);
		order.setOrderType(OrderType.SELL);
		
		Order savedOrder= orderRepository.save(order);
		
		// drop dust positions worth $1 or less
		if(updatedAsset.getQuantity() * coin.getCurrentPrice() <= 1)
		{
			assetService.deleteAsset(updatedAsset.getId());
		}
		
		return savedOrder;
	}
	
	
	
	
	
	@Transactional(rollbackFor = Exception.class)
	public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception
	{
		if (orderType == null)
		{
			throw new Exception("Invalid order Type");
		}
		
		if (orderType.equals(OrderType.BUY))
		{
			return buyAsset(coin, quantity, user);
		}
		else if(orderType.equals(OrderType.SELL) )
		{
			return sellAsset(coin, quantity, user);
		}
		else
		{
			throw new Exception("Invalid order Type");
		}
		
	}

}
