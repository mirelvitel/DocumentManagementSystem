import { render, screen } from '@testing-library/react';
import App from './App';

test('renders DocScan brand in navbar', () => {
  render(<App />);
  const brandElement = screen.getByText(/DocScan/i);
  expect(brandElement).toBeInTheDocument();
});

test('renders navigation links', () => {
  render(<App />);
  expect(screen.getByText(/Documents/i)).toBeInTheDocument();
  expect(screen.getByText(/Upload/i)).toBeInTheDocument();
});