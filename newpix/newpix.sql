-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Tempo de geração: 21/10/2025 às 02:15
-- Versão do servidor: 10.4.32-MariaDB
-- Versão do PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Banco de dados: `newpix`
--

-- --------------------------------------------------------

--
-- Estrutura para tabela `sessoes`
--

CREATE TABLE `sessoes` (
  `token` varchar(255) NOT NULL,
  `usuario_id` int(11) NOT NULL,
  `criado_em` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `sessoes`
--

INSERT INTO `sessoes` (`token`, `usuario_id`, `criado_em`) VALUES
('4f1ac976-c53d-43db-b083-8b753f92c8c4', 2, '2025-09-14 17:15:34'),
('7328d482-089f-4a1b-91b2-cf484ccc6486', 3, '2025-09-14 17:07:45'),
('898f26f6-ea7b-48ea-bff1-9c00bb8eb160', 2, '2025-09-14 17:23:37'),
('cb3327ee-8d07-4aa7-86a1-724ae6e474eb', 3, '2025-09-14 17:15:48'),
('ccb45283-4dcc-45bd-9d46-2c38672b1952', 2, '2025-09-14 17:06:45');

-- --------------------------------------------------------

--
-- Estrutura para tabela `transacoes`
--

CREATE TABLE `transacoes` (
  `id` int(11) NOT NULL,
  `id_remetente` int(11) NOT NULL,
  `id_destinatario` int(11) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `data` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `transacoes`
--

INSERT INTO `transacoes` (`id`, `id_remetente`, `id_destinatario`, `valor`, `data`) VALUES
(1, 1, 2, 1.00, '2025-09-14 23:28:14'),
(2, 2, 1, 123.00, '2025-09-15 02:21:11'),
(3, 2, 1, 11.00, '2025-09-15 02:21:35'),
(4, 2, 1, 1001.00, '2025-09-15 02:21:57'),
(5, 1, 2, 1.00, '2025-09-15 02:22:05'),
(6, 1, 2, 12312.00, '2025-09-15 03:50:51'),
(7, 1, 2, 300000.00, '2025-09-15 03:51:15'),
(8, 2, 1, 100000.00, '2025-09-15 03:52:05'),
(9, 2, 1, 1.00, '2025-09-15 04:00:40'),
(10, 1, 2, 123123.00, '2025-09-15 04:38:54'),
(11, 1, 2, 123.12, '2025-09-15 04:39:05'),
(12, 1, 2, 105.40, '2025-09-15 05:00:57'),
(13, 1, 2, 150.12, '2025-09-15 05:01:36'),
(14, 1, 2, 1.00, '2025-09-15 05:17:47'),
(15, 1, 2, 13230.00, '2025-09-15 05:17:54'),
(16, 1, 2, 23320.00, '2025-09-15 05:46:30'),
(17, 1, 2, 10000.00, '2025-09-24 23:17:47'),
(18, 1, 2, 110.00, '2025-09-25 22:01:17'),
(19, 1, 2, 1230.00, '2025-10-12 18:55:29'),
(20, 10, 11, 50.00, '2025-10-13 12:45:06');

-- --------------------------------------------------------

--
-- Estrutura para tabela `usuarios`
--

CREATE TABLE `usuarios` (
  `id` int(11) NOT NULL,
  `nome` varchar(120) NOT NULL,
  `cpf` varchar(14) NOT NULL,
  `senha` varchar(120) NOT NULL,
  `saldo` decimal(10,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Despejando dados para a tabela `usuarios`
--

INSERT INTO `usuarios` (`id`, `nome`, `cpf`, `senha`, `saldo`) VALUES
(1, 'Lucas Gonçalves Padilha', '123.123.123-00', '123123', 106648.59),
(2, 'Gabrielly', '123.123.123-01', '123123', 383927.64),
(3, 'Sebastiao', '123.123.123-02', '123123', 412.00),
(6, 'Lucas Daniel Teste', '987.987.987-98', '0123456789', 0.00),
(7, '\'Testando\'', '555.555.444-41', '123123', 0.00),
(10, 'Eduardo Andrade Teste', '777.777.777-77', '123456', 5.00),
(11, 'Eduardo Andrade2', '111.111.111-11', '123456', 50.00);

--
-- Índices para tabelas despejadas
--

--
-- Índices de tabela `sessoes`
--
ALTER TABLE `sessoes`
  ADD PRIMARY KEY (`token`),
  ADD KEY `usuario_id` (`usuario_id`);

--
-- Índices de tabela `transacoes`
--
ALTER TABLE `transacoes`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_remetente` (`id_remetente`),
  ADD KEY `id_destinatario` (`id_destinatario`);

--
-- Índices de tabela `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `cpf` (`cpf`);

--
-- AUTO_INCREMENT para tabelas despejadas
--

--
-- AUTO_INCREMENT de tabela `transacoes`
--
ALTER TABLE `transacoes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT de tabela `usuarios`
--
ALTER TABLE `usuarios`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Restrições para tabelas despejadas
--

--
-- Restrições para tabelas `sessoes`
--
ALTER TABLE `sessoes`
  ADD CONSTRAINT `sessoes_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;

--
-- Restrições para tabelas `transacoes`
--
ALTER TABLE `transacoes`
  ADD CONSTRAINT `transacoes_ibfk_1` FOREIGN KEY (`id_remetente`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `transacoes_ibfk_2` FOREIGN KEY (`id_destinatario`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
